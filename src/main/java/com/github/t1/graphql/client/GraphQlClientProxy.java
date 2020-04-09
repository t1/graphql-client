package com.github.t1.graphql.client;

import com.github.t1.graphql.client.api.GraphQlClientException;
import com.github.t1.graphql.client.api.GraphQlClientHeader;
import com.github.t1.graphql.client.json.JsonReader;
import com.github.t1.graphql.client.reflection.FieldInfo;
import com.github.t1.graphql.client.reflection.MethodInfo;
import com.github.t1.graphql.client.reflection.ParameterInfo;
import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.t1.graphql.client.CollectionUtils.toMultivaluedMap;
import static java.util.stream.Collectors.joining;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;
import static lombok.AccessLevel.PACKAGE;

@Slf4j
@RequiredArgsConstructor(access = PACKAGE)
class GraphQlClientProxy {

    private final WebTarget target;
    private final List<GraphQlClientHeader> headers;

    Object invoke(MethodInfo method) {
        String request = request(method);

        log.info("request graphql: {}", request);
        String response = post(request);
        log.info("response graphql: {}", response);

        return fromJson(method, request, response);
    }

    private String request(MethodInfo method) {
        JsonObjectBuilder request = Json.createObjectBuilder();
        request.add("query", operation(method) +
            " { " + graphQlRequest(method) +
            fields(method.getReturnType())
            + " }");
        return request.build().toString();
    }

    private String operation(MethodInfo method) {
        return method.isQuery() ? "query" : "mutation";
    }

    private String graphQlRequest(MethodInfo method) {
        StringBuilder request = new StringBuilder(method.getName());
        if (method.getParameterCount() > 0) {
            request.append(method.getParameters().stream()
                .map(this::param)
                .collect(joining(", ", "(", ")")));
        }
        return request.toString();
    }

    private String param(ParameterInfo parameter) {
        StringBuilder out = new StringBuilder();
        buildParam(out, parameter.getName(), parameter.getType(), parameter.getValue());
        return out.toString();
    }

    private void buildParam(StringBuilder out, String name, TypeInfo type, Object value) {
        out.append(name).append(": ");
        if (value instanceof Boolean || value instanceof Number)
            out.append(value);
        else if (type.isScalar())
            out.append("\"").append(value).append("\""); // TODO this could be a security issue
        else if (type.isCollection()) {
            out.append("[");
            out.append("]");
        } else {
            out.append("{");
            AtomicBoolean first = new AtomicBoolean(true);
            type.fields().forEach(field -> {
                if (first.get())
                    first.set(false);
                else
                    out.append(", ");
                buildParam(out, field.getName(), field.getType(), field.get(value));
            });
            out.append("}");
        }
    }

    private String fields(TypeInfo type) {
        while (type.isOptional())
            type = type.getItemType();
        if (type.isScalar()) {
            return "";
        } else if (type.isCollection()) {
            return fields(type.getItemType());
        } else {
            return type.fields()
                .map(this::field)
                .collect(joining(" ", " {", "}"));
        }
    }

    private String field(FieldInfo field) {
        TypeInfo type = field.getType();
        if (type.isScalar() || type.isCollection() && type.getItemType().isScalar()) {
            return field.getName();
        } else {
            return field.getName() + fields(type);
        }
    }

    private String post(String request) {
        Response response = target
            .request(APPLICATION_JSON_TYPE)
            .headers(buildHeaders())
            .post(Entity.json(request));
        StatusType status = response.getStatusInfo();
        if (status.getFamily() != SUCCESSFUL)
            throw new GraphQlClientException("expected successful status code but got " +
                status.getStatusCode() + " " + status.getReasonPhrase() + ":\n" +
                response.readEntity(String.class));
        return response.readEntity(String.class);
    }

    private MultivaluedMap<String, Object> buildHeaders() {
        return headers.stream()
            .peek(header -> log.debug("add header '{}'", header.getName())) // don't log values; could contain tokens
            .map(GraphQlClientHeader::toEntry)
            .collect(toMultivaluedMap());
    }

    private Object fromJson(MethodInfo method, String request, String response) {
        JsonObject responseJson = readResponse(request, response);
        JsonValue value = getData(method, responseJson);
        return JsonReader.readFrom(method, value);
    }

    private JsonObject readResponse(String request, String response) {
        JsonObject responseJson = Json.createReader(new StringReader(response)).readObject();
        if (responseJson.containsKey("errors") && !responseJson.isNull("errors"))
            throw new GraphQlClientException("errors from service: " + responseJson.getJsonArray("errors") + ":\n  " + request);
        return responseJson;
    }

    private JsonValue getData(MethodInfo method, JsonObject responseJson) {
        JsonObject data = responseJson.getJsonObject("data");
        if (!data.containsKey(method.getName()))
            throw new GraphQlClientException("no data for '" + method.getName() + "':\n  " + data);
        return data.get(method.getName());
    }
}
