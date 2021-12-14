package ru.ramprox.server.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Cookie implements Serializable {
    private final String name;
    private String value;
    private String domain;
    private LocalDateTime expires;
    private long maxAge = DEFAULT_MAX_AGE;
    private String path;
    private boolean httpOnly;

    private static final long DEFAULT_MAX_AGE = -1L;
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss 'GMT'",
                    new Locale("en", "EN")).withZone(ZoneOffset.UTC);

    public static final String JSESSION_ID = "JSESSIONID";
    public static final String REQUESTED_PAGE = "RequestedPage";

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDomain() {
        return domain;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public String getPath() {
        return path;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append("=").append(value).append(";");
        if(maxAge != DEFAULT_MAX_AGE) {
            builder.append(" Max-Age").append("=").append(maxAge).append(";");
        }
        if(expires != null) {
            builder.append(" Expires").append("=").append(expires.format(formatter)).append(";");
        }
        if(path != null) {
            builder.append(" Path").append("=").append(path).append(";");
        }
        if(domain != null) {
            builder.append(" Domain").append("=").append(domain).append(";");
        }
        if(httpOnly) {
            builder.append(" HttpOnly").append(";");
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cookie cookie = (Cookie) o;

        if (!name.equals(cookie.name)) return false;
        return value.equals(cookie.value);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    public static class Builder {
        private Cookie cookie;

        public Builder(String name, String value) {
            this.cookie = new Cookie(name, value);
        }

        public Builder withDomain(String domain) {
            this.cookie.domain = domain;
            return this;
        }

        public Builder withExpires(LocalDateTime expires) {
            this.cookie.expires = expires;
            return this;
        }

        public Builder withMaxAge(long maxAge) {
            this.cookie.maxAge = maxAge;
            return this;
        }

        public Builder withPath(String path) {
            this.cookie.path = path;
            return this;
        }

        public Builder withHttpOnly() {
            this.cookie.httpOnly = true;
            return this;
        }

        public Cookie build() {
            String name = this.cookie.getName();
            String value = this.cookie.getValue();
            if(name == null || name.isEmpty()) {
                throw new IllegalStateException("Cookie without name");
            }
            if(value == null || value.isEmpty()) {
                throw new IllegalStateException("Cookie without value");
            }
            return this.cookie;
        }
    }
}