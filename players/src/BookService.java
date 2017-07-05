import org.apache.axiom.om.OMElement;

public class BookService {

    public String addBook(String name, Book book) {
        System.out.println(name);
        System.out.println(book.getAuthors());
        return "added";
    }

    public String updateBook(String name, Book book) {
        System.out.println(name);
        System.out.println(book.getAuthors());
        return "updated";
    }

    public String addAuthors(String name, Author authors[]) {
        System.out.println(name);
        System.out.println(authors);
        return "added";
    }

    public String addAuthorsWrapped(String name, Authors authors) {
        System.out.println(name);
        System.out.println(authors.getAuthor());
        return "added";
    }

    public String addBookOM(OMElement message) {
        System.out.println(message);
        return "added";
    }

}
