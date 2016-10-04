import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Book")
@XmlAccessorType(XmlAccessType.FIELD)
public class Book {
    // XmLElementWrapper generates a wrapper element around XML representation
    @XmlElementWrapper(name = "authors")
    // XmlElement sets the name of the entities
    @XmlElement(name = "author")
    private Author[] authors;
    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "isbn")
    private String isbn;

    public void setName(String name) {
        this.name = name;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setAuthors(Author[] authors) {
        this.authors = authors;
    }

    public Author[] getAuthors() {
        return authors;
    }

    public String getName() {
        return name;
    }

    public String getIsbn() {
        return isbn;
    }
}
