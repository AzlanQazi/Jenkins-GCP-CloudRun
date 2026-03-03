pipeline {
    agent any
    tools {
	    jdk 'java2109'
	    maven 'maven399'
    }
	
    environment {
		SONAR_SCANNER_HOME = tool 'sonar7'
		IMAGE_NAME = "java-app"
        IMAGE_TAG = "${BUILD_NUMBER}"
	    GCP_PROJECT_ID = "project-8607a4d0-e597-411f-9c2"
	    FULL_IMAGE_NAME = "us-docker.pkg.dev/${GCP_PROJECT_ID}/java-app-repo-02/${IMAGE_NAME}:${IMAGE_TAG}"
	    SERVICE_NAME = "java-app-service"
	    REGION = "us-central1"
    }
	
    stages {
		
        stage('Initialize Pipeline'){
            steps {
                echo 'Initializing Pipeline ...'
		        sh 'java -version'
		        sh 'mvn -version'
            }
        }
		
        stage('Checkout GitHub Codes'){
            steps {
                echo 'Checking out GitHub Codes ...'
		        checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'jenkins-gcp', url: 'https://github.com/AzlanQazi/Jenkins-GCP-CloudRun.git']])
            }
        }
		
        stage('Maven Build'){
            steps {
                echo 'Building Java App with Maven'	
		        sh 'mvn clean package'
            }
        }
		
        stage('JUnit Test of Java App'){
            steps {
                echo 'JUnit Testing'
		        sh 'mvn test'
            }
        }
		
        stage('SonarQube Analysis'){
            steps {
                echo 'Running Static Code Analysis with SonarQube'
				withCredentials([string(credentialsId: 'sonartoken', variable: 'sonarToken')]) {
					withSonarQubeEnv('sonar') {
						sh """
						   ${SONAR_SCANNER_HOME}/bin/sonar-scanner \
                           -Dsonar.projectKey=jenkinsgcp \
                           -Dsonar.sources=. \
                           -Dsonar.host.url=http://sonarqube-dind:9000 \
						   -Dsonar.java.binaries=target/classes \
                           -Dsonar.token=$sonarToken
						"""
                    }
                }
            }
        }
		
        stage('Trivy FS Scan'){
            steps {
                echo 'Scanning File System with Trivy FS ...'
		        sh 'trivy fs --format table -o FSScanReport.html'
            }
        }
		
        stage('Build & Tag Docker Image'){
            steps {
                echo 'Building the Java App Docker Image'
		        script {
					sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
				}
            }
        }
		
        stage('Trivy Security Scan'){
            steps {
                echo 'Scanning Docker Image with Trivy'
				// 1. Save the image to a file (this uses the Docker client, which works)
                sh "docker save ${IMAGE_NAME}:${IMAGE_TAG} -o java-app.tar"
            
                // 2. Tell Trivy to scan the file directly using the --input (or -i) flag
                sh "trivy image --severity HIGH,CRITICAL --no-progress --format table -o trivyImageScanReport.html --input java-app.tar"
            
                // 3. Clean up the file so it doesn't take up space on your Jenkins server
                sh "rm java-app.tar"
				
		        //sh "trivy image --severity HIGH,CRITICAL --no-progress --format table -o trivyFSScanReport.html imager ${IMAGE_NAME}:${IMAGE_TAG}"
				//--cache-dir ${WORKSPACE}/.trivy-cache 
            }
        }
		
	    stage('Authenticate with GCP, Tag & Push to Artifact Registry') {
        	steps {
				echo 'Authenticate with GCP, tag and Push Image to Artifact Registry'
				withCredentials([file(credentialsId: 'gcpjmsa', variable: 'gcpCred')]) { 
					withEnv(["GOOGLE_APPLICATION_CREDENTIALS=${gcpCred}"]) {
        				sh """
        					echo "Activating GCP service account..."
       						gcloud auth activate-service-account --key-file="${GOOGLE_APPLICATION_CREDENTIALS}"
        					gcloud config set project "${GCP_PROJECT_ID}"
      	  					echo "Configuring Docker to use gcloud credentials..."
        					gcloud auth configure-docker us-docker.pkg.dev --quiet
    					"""
						script {
							sh """
								gcloud artifacts repositories create java-app-repo-${IMAGE_TAG} --repository-format=docker --location=us --description="Docker repository" --project=$GCP_PROJECT_ID
     						"""
							sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${FULL_IMAGE_NAME}"
							sh "docker push ${FULL_IMAGE_NAME}"
							echo "Image pushed to: ${FULL_IMAGE_NAME}"
						}
					}
				}
            }
        }
		
		stage('Deploy to Cloud Run') {
			steps {
				echo 'Deploying Image to Google Cloud Run'
				withCredentials([file(credentialsId: 'gcpjmsa', variable: 'gcpCred')]) { 
						withEnv(["GOOGLE_APPLICATION_CREDENTIALS=${gcpCred}"]) {
							sh """
								gcloud run deploy ${SERVICE_NAME} \
            					--image=${FULL_IMAGE_NAME} \
            					--region=${REGION} \
            					--platform=managed \
            					--allow-unauthenticated \
		 						--port=8090 \
            					--memory=512Mi \
            					--quiet
							"""
						}
				}
			}
		}
		
		stage('Get Cloud Run Service URL') {
       	 	steps {
				echo 'Getting Cloud Run Service URL'
				withCredentials([file(credentialsId: 'gcpjmsa', variable: 'gcpCred')]) { 
						withEnv(["GOOGLE_APPLICATION_CREDENTIALS=${gcpCred}"]) {
							sh """
                    			SERVICE_URL=\$(gcloud run services describe ${SERVICE_NAME} \
                        			--platform managed \
                        			--region ${REGION} \
                        			--format="value(status.url)")
			    				echo "Service deployed successfully!"
                        		echo "Service URL: \${SERVICE_URL}"
                			"""
						}
				}
            }
       	}
		
	}
}
