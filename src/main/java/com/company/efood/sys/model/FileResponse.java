package com.company.efood.sys.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponse {
    private String filename;
    private String url;

    public FileResponse(String filename, String url) {
        this.filename = filename;
        this.url = url;
    }



}

