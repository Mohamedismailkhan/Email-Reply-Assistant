package com.email.Writer.io;

import lombok.Data;

@Data
public class EmailRequest {
    private String emailContent;
    private String tone;
}
