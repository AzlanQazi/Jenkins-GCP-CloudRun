package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class HelloWorldTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private HelloWorld app;
    
    @Test
    public void testGetMessage() {
        assertEquals("It worked on my machine. And surprisingly, it works here too!<br>" +
                "Jenkins did the heavy lifting; I’m just here for the traffic.<br>" +
                "This app was hand-delivered by a very hardworking pipeline.<br>" +
                "Warning: May contain traces of late-night debugging.", app.getMessage());
    }
    
    @Test
    public void testHelloEndpoint() throws Exception {
        this.mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(
                    "It worked on my machine. And surprisingly, it works here too!<br>" +
                    "Jenkins did the heavy lifting; I’m just here for the traffic.<br>" +
                    "This app was hand-delivered by a very hardworking pipeline.<br>" +
                    "Warning: May contain traces of late-night debugging.")));
    }
}
