# Markdown content producer

Markdown conent producer processes requests for help resources ending with ``.md.html`` (e.g. ``overview.md.html``) by finding a corresponding markdown resources without ``.html``
suffix (e.g. ``overview.md``) and converting them to HTML. 

The producer shall be registered with the help system as shown below:

```xml
<extension
      id="... ID here ..."
      name="Markdown Content Producer"
      point="org.eclipse.help.contentProducer">
   <contentProducer
         producer="org.nasdanika.help.markdown.MarkdownContentProducer">
   </contentProducer>
</extension>
``` 

The producer is already registered by ``org.nasdanika.help`` bundle and can be referenced from another bundle as shown below:

```xml
<extension
      point="org.eclipse.help.contentProducer">
   <binding
         producerId="org.nasdanika.help.MarkdownContentProducer">
   </binding>
</extension>
``` 

## Extensions

The content producer supports several extensions:

* Pre-processor - processes resource content before passing it to the markdown processor.
* Wiki link resolver - resolves wiki links with ``[[...]]`` syntax.
* Wiki link processor - processes wiki links with ``[[...]]`` syntax. Processors can be used to add icons to links or open links in a dialog box.

### PlantUML pre-processor

``org.nasdanika.help`` bundle provides [PlantUML](http://plantuml.com) pre-processor which replaces diagram definitions between ``@startuml`` and ``@enduml`` tokens with diagram images. The opening tag shall be preceded by at least on blank line and the closing tag shall be followed by at least one blank line. 

#### Example

Definition
```
@startuml
	Alice -> Bob
@enduml
```	 

Diagram (shall be opened in the Eclipse Help System)

@startuml
	Alice -> Bob
@enduml




## TODO

Extensions - same plugin policy for apidocs.





