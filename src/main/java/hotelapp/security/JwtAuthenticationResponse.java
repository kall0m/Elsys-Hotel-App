package hotelapp.security;

import hotelapp.models.Worker;

public class JwtAuthenticationResponse {
    private int userType;
    private Worker worker = null;
    private String accessToken;
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public JwtAuthenticationResponse(String accessToken, Worker worker, int userType) {
        this.accessToken = accessToken;
        this.worker = worker;
        this.userType = userType;
    }

    public JwtAuthenticationResponse(String accessToken, int userType) {
        this.accessToken = accessToken;
        this.userType = userType;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
