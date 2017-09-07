# BATCH CONTENT UPDATE VIA REST
This is an example OSGi plugin that will let you update 1 field on a contentlet or a list of contentlets specified by the query you pass in. All other properties of the updated contents will remain the same.  This provides an example of easy way to do batch updates. 

To use it, send a json object in this form:
```
{
    "field": "THE FIELD VARIABLE YOU WANT TO UPDATE",
    "value": "THE NEW VALUE AS A STRING- you can pass in a bool, date, string, int and the system will deal with it",
    "query": "THE LUCENE QUERY TO SPECIFY WHAT CONTENT YOU WANT TO UPDATE",
    "publish" : true or false
}

```

### Updates the byline field on a news item on demo site without publishing the changes
```
curl -v -u admin@dotcms.com:admin -XPUT http://localhost:8080/api/contentUpdate/update -H "Content-Type: application/json" -H "Accept: application/json" -d '{
    "field": "byline",
    "value": "TEST UPDATE",
    "query": "+identifier:2943b5eb-9105-4dcf-a1c7-87a9d4dc92a6"
}'
```
### Updates the byline field on a news item on demo site  and then publishes the changes
```
curl -v -u admin@dotcms.com:admin -XPUT http://localhost:8080/api/contentUpdate/update -H "Content-Type: application/json" -H "Accept: application/json" -d '{
    "field": "byline",
    "value": "TEST PUBLISH",
    "query": "+identifier:2943b5eb-9105-4dcf-a1c7-87a9d4dc92a6",
    "publish" : true
}'
```

### Updates the publish date on a blog entry on demo site  and then publishes the changes
```
curl -v -u admin@dotcms.com:admin -XPUT http://localhost:8080/api/contentUpdate/update -H "Content-Type: application/json" -H "Accept: application/json" -d '{
    "field": "sysPublishDate",
    "value": "1/13/1972 1:00:00",
    "query": "+identifier:146f45d6-94e1-4abe-9516-05e1e265efec",
    "publish" : true
}'
```
### Updates the publish date on ALL blog entries on demo site  and then publishes the changes
```
curl -v -u admin@dotcms.com:admin -XPUT http://localhost:8080/api/contentUpdate/update -H "Content-Type: application/json" -H "Accept: application/json" -d '{
    "field": "sysPublishDate",
    "value": "1/1/2001 1:00:00",
    "query": "+contentType:Blog",
    "publish" : true
}'
```



