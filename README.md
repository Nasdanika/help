# Nasdanika Help

Provides:

* The primary toc for other Nasdanika help toc's to link to.
* Markdown content producer which converts intercepts ``*.md.html`` help resource requests, finds corresponding ``*.md`` files and converts them to HTML on the fly.
* Markdown processor extensions such as:
    * Pre-processors
        * [PlantUML](http://plantuml.com) diagram pre-processor replaces digram definitions between ``@startuml`` and ``@enduml`` tags with generated diagrams (SVG).
    * Wiki link resolvers.
        * JavaDoc wiki link resolver is work in progress - replaces ``[[javadoc>fully qualified class name]]`` with a link to API documentation.
    * Wiki link processors. 
    
## Resources

* [Help](org.nasdanika.help/doc/markdown-content-producer.md)
* P2 repository - ``http://www.nasdanika.org/products/help/repository``
* [API documentation](http://www.nasdanika.org/products/help/apidocs)
    
