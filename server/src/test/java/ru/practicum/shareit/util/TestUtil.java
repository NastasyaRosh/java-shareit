package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@UtilityClass
public class TestUtil {
    public static final MediaType DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_HEADER = "X-Sharer-User-Id";

    public static final ResultMatcher OK = status().isOk();
    public static final ResultMatcher NOT_FOUND = status().isNotFound();

    public static MockHttpServletRequestBuilder getGetReq(String path, Long headerId) {
        MockHttpServletRequestBuilder req = get(path)
                .characterEncoding(DEFAULT_CHARSET)
                .contentType(DEFAULT_MEDIA_TYPE)
                .accept(DEFAULT_MEDIA_TYPE);

        if (headerId != null) {
            req.header(DEFAULT_HEADER, headerId);
        }
        return req;
    }

    public static MockHttpServletRequestBuilder getPostReq(String path, Long headerId) {
        MockHttpServletRequestBuilder req = post(path)
                .characterEncoding(DEFAULT_CHARSET)
                .contentType(DEFAULT_MEDIA_TYPE)
                .accept(DEFAULT_MEDIA_TYPE);

        if (headerId != null) {
            req.header(DEFAULT_HEADER, headerId);
        }
        return req;
    }

    public static MockHttpServletRequestBuilder getPatchReq(String path, Long headerId) {
        MockHttpServletRequestBuilder req = patch(path)
                .characterEncoding(DEFAULT_CHARSET)
                .contentType(DEFAULT_MEDIA_TYPE)
                .accept(DEFAULT_MEDIA_TYPE);

        if (headerId != null) {
            req.header(DEFAULT_HEADER, headerId);
        }
        return req;
    }
}

