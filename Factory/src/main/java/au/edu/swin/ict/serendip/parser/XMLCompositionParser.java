package au.edu.swin.ict.serendip.parser;

import au.edu.swin.ict.serendip.composition.Composition;

import java.io.File;

//TODO:Check if this is neccesssary. If not remove. 
public class XMLCompositionParser {
    File xmlFile = null;

    public XMLCompositionParser(File xmlFile) {
        super();
        this.xmlFile = xmlFile;
    }

    public Composition parse() {

        // CompositionDocument compDoc = null;;
        // try {
        // compDoc = CompositionDocument.Factory.parse(xmlFile);
        // } catch (XmlException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        return new Composition(null);

    }

}
