package com.namha.expensemanagement.viewmodels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class MessageModelTest {

    @Test
    public void testConstructorAndGetters() {
        MessageModel message = new MessageModel("Hello", MessageModel.SENT_BY_ME);

        assertNotNull(message);
        assertEquals("Hello", message.getMessage());
        assertEquals(MessageModel.SENT_BY_ME, message.getSentBy());
    }

    @Test
    public void testSetters() {
        MessageModel message = new MessageModel("Hi", MessageModel.SENT_BY_BOT);

        message.setMessage("New message");
        message.setSentBy(MessageModel.SENT_BY_ME);

        assertEquals("New message", message.getMessage());
        assertEquals(MessageModel.SENT_BY_ME, message.getSentBy());
    }
}
