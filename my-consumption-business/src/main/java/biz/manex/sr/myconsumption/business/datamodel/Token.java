package biz.manex.sr.myconsumption.business.datamodel;

/**
 * Created by Patrick Herbeuval on 23/04/14.
 */
public class Token {
    private String deviceType;
    private String token;

    public Token() {
    }

    public Token(String deviceType, String token) {
        this.deviceType = deviceType;
        this.token = token;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isThisId(String id) {
        return this.token.equals(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token1 = (Token) o;

        if (deviceType != null ? !deviceType.equals(token1.deviceType) : token1.deviceType != null) return false;
        if (token != null ? !token.equals(token1.token) : token1.token != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = deviceType != null ? deviceType.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }
}
