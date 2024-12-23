package com.example.story_teller;

public class TtsRequest {
    private App app;
    private User user;
    private Audio audio;
    private Request request;

    public TtsRequest() {
        this.app = new App();
        this.user = new User();
        this.audio = new Audio();
        this.request = new Request();
    }

    public void setText(String text) {
        this.request.text = text;
    }

    public static class App {
        private String appid = "xxx"; // 替换为你的 appid
        private String cluster = "volcano_tts";
    }

    public static class User {
        private String uid = "uid";
    }

    public static class Audio {
        private String encoding = "mp3";
        private String voice_type = "BV001_streaming";
        private Double speed_ratio = 1.0;
        private Double volume_ratio = 1.0;
        private Double pitch_ratio = 1.0;
    }

    public static class Request {
        private String reqid = java.util.UUID.randomUUID().toString();
        private String text = "";
        private String operation = "query";
    }
}
