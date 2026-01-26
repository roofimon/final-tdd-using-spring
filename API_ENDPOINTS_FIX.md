# API Endpoints Fix - What Made It Work

## Problem Summary
The REST API endpoints were not working properly. Requests to `/odtBank/rest/account/{id}` and `/odtBank/rest/account/{srcId}/transfer/{amount}/to/{destId}` were failing with errors related to view rendering and missing JSTL classes.

## Root Cause
The `AccountController` methods were returning domain objects (`Account` and `TransferReceipt`) without the `@ResponseBody` annotation. This caused Spring to:
1. Treat the returned objects as **view names** instead of response bodies
2. Attempt to render them as JSP views using `InternalResourceView`/`JstlView`
3. Fail with `ClassNotFoundException: javax.servlet.jsp.jstl.core.Config` because JSTL was not in the classpath

## Solution: Add `@ResponseBody` Annotation

### Before (Broken)
```java
@RequestMapping(value = "/{id}", method = RequestMethod.GET)
public Account handleById(@PathVariable("id") String accId) {
    return repository.findById(accId);
}

@RequestMapping(value = "/{srcId}/transfer/{amount}/to/{destId}")
public TransferReceipt handleTransfer(@PathVariable("srcId") String srcId,
        @PathVariable("amount") double amount,
        @PathVariable("destId") String destId) throws InsufficientFundsException {
    return service.transfer(amount, srcId, destId);
}
```

### After (Working)
```java
@RequestMapping(value = "/{id}", method = RequestMethod.GET)
@ResponseBody  // ← Added annotation
public Account handleById(@PathVariable("id") String accId) {
    return repository.findById(accId);
}

@RequestMapping(value = "/{srcId}/transfer/{amount}/to/{destId}")
@ResponseBody  // ← Added annotation
public TransferReceipt handleTransfer(@PathVariable("srcId") String srcId,
        @PathVariable("amount") double amount,
        @PathVariable("destId") String destId) throws InsufficientFundsException {
    return service.transfer(amount, srcId, destId);
}
```

### Also Added Import
```java
import org.springframework.web.bind.annotation.ResponseBody;
```

## Why `@ResponseBody` Works

The `@ResponseBody` annotation tells Spring to:
1. **Serialize the returned object** using configured message converters (in this case, Jackson for JSON)
2. **Write the serialized content** directly to the HTTP response body
3. **Skip view resolution** entirely (no attempt to find JSP files or render views)

Since the Spring XML config includes `<mvc:annotation-driven>` with `MappingJackson2HttpMessageConverter`, Jackson automatically serializes the domain objects to JSON.

## Supporting Configuration

### Spring MVC Configuration (spring-rest-servlet.xml)
```xml
<mvc:annotation-driven>
    <mvc:message-converters register-defaults="true">
        <bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter" />
    </mvc:message-converters>
</mvc:annotation-driven>
```

### Dispatcher Servlet Mapping (web.xml)
```xml
<servlet-mapping>
    <servlet-name>rest</servlet-name>
    <url-pattern>/rest/*</url-pattern>
</servlet-mapping>
```

Note: Endpoints are under `/rest/` path, so the full URL is `/odtBank/rest/account/{id}`

## Test Results

### Working Endpoints

#### 1. Get Account (Initial State)
```bash
$ curl -s http://localhost:8080/odtBank/rest/account/A123
{"id":"A123","balance":100.00}
```

#### 2. Get Another Account
```bash
$ curl -s http://localhost:8080/odtBank/rest/account/C456
{"id":"C456","balance":0.00}
```

#### 3. Transfer Operation
```bash
$ curl -s http://localhost:8080/odtBank/rest/account/A123/transfer/50/to/C456
{
  "transferAmount":50.0,
  "feeAmount":8.35,
  "finalSourceAccount":{"id":"A123","balance":41.65},
  "finalDestinationAccount":{"id":"C456","balance":50.0}
}
```

#### 4. Verify Balances After Transfer
```bash
$ curl -s http://localhost:8080/odtBank/rest/account/A123
{"id":"A123","balance":41.65}

$ curl -s http://localhost:8080/odtBank/rest/account/C456
{"id":"C456","balance":50.0}
```

## Key Takeaways

1. **`@ResponseBody` is critical** for REST endpoints to return JSON/XML instead of views
2. **Dispatcher servlet mapping** determines URL path (`/rest/*` in this case)
3. **Message converters** handle object serialization (Jackson for JSON)
4. **Domain objects must be serializable** (have getters for Jackson to work)

## Alternative: Use `@RestController`

A more modern approach would be to use `@RestController` instead of `@Controller + @ResponseBody`:

```java
@RestController
@RequestMapping("/account")
public class AccountController {
    // methods automatically return JSON, no @ResponseBody needed on each method
    
    @GetMapping("/{id}")
    public Account handleById(@PathVariable("id") String accId) {
        return repository.findById(accId);
    }
}
```

This implicitly adds `@ResponseBody` to all methods in the class.

---

**Date**: January 26, 2026  
**Status**: ✅ All endpoints working  
**Container**: Running on Java 21, Tomcat 9.0.89, Spring 5.3.36
