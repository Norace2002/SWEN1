package at.fhtw.httpserver.server;

import at.fhtw.httpserver.http.Method;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Request {
    private Method method;
    private String urlContent;
    private String pathname;
    private List<String> pathParts;
    private String params;
    private HeaderMap headerMap =  new HeaderMap();
    private String body;

    public String getServiceRoute(){
        String path = "/";

        if (this.pathParts == null || this.pathParts.isEmpty()) {
            return null;
        }

        for(String part: this.getPathParts()){
            path = path + part + '/';
        }

        path = path.substring(0, path.length()-1);

        return path;
    }

    public void setUrlContent(String urlContent) {
        this.urlContent = urlContent;
        boolean hasParams = urlContent.contains("?");

        if (hasParams) {
            String[] pathParts =  urlContent.split("\\?");
            this.setPathname(pathParts[0]);
            this.setParams(pathParts[1]);
        }
        else
        {
            this.setPathname(urlContent);
            this.setParams(null);
        }
    }

    public void setMethod(Method method) {
        this.method = method;
    }


    public void setPathname(String pathname) {
        this.pathname = pathname;
        String[] stringParts = pathname.split("/");
        this.pathParts = new ArrayList<>();
        for (String part :stringParts)
        {
            if (part != null &&
                    part.length() > 0)
            {
                this.pathParts.add(part);
            }
        }

    }

    public void setParams(String params) {
        this.params = params;
    }

    public void setHeaderMap(HeaderMap headerMap) {
        this.headerMap = headerMap;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setPathParts(List<String> pathParts) {
        this.pathParts = pathParts;
    }
}
