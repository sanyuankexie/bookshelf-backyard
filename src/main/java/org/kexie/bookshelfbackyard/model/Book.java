package org.kexie.bookshelfbackyard.model;

public class Book {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column books.guid
     *
     * @mbg.generated Mon Jun 28 15:47:04 CST 2021
     */
    private String guid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column books.name
     *
     * @mbg.generated Mon Jun 28 15:47:04 CST 2021
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column books.author
     *
     * @mbg.generated Mon Jun 28 15:47:04 CST 2021
     */
    private String author;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column books.isbn
     *
     * @mbg.generated Mon Jun 28 15:47:04 CST 2021
     */
    private String isbn;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column books.guid
     *
     * @return the value of books.guid
     *
     * @mbg.generated Mon Jun 28 15:47:04 CST 2021
     */
    public String getGuid() {
        return guid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column books.guid
     *
     * @param guid the value for books.guid
     *
     * @mbg.generated Mon Jun 28 15:47:04 CST 2021
     */
    public void setGuid(String guid) {
        this.guid = guid == null ? null : guid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column books.name
     *
     * @return the value of books.name
     *
     * @mbg.generated Mon Jun 28 15:47:04 CST 2021
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column books.name
     *
     * @param name the value for books.name
     *
     * @mbg.generated Mon Jun 28 15:47:04 CST 2021
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column books.author
     *
     * @return the value of books.author
     *
     * @mbg.generated Mon Jun 28 15:47:04 CST 2021
     */
    public String getAuthor() {
        return author;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column books.author
     *
     * @param author the value for books.author
     *
     * @mbg.generated Mon Jun 28 15:47:04 CST 2021
     */
    public void setAuthor(String author) {
        this.author = author == null ? null : author.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column books.isbn
     *
     * @return the value of books.isbn
     *
     * @mbg.generated Mon Jun 28 15:47:04 CST 2021
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column books.isbn
     *
     * @param isbn the value for books.isbn
     *
     * @mbg.generated Mon Jun 28 15:47:04 CST 2021
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn == null ? null : isbn.trim();
    }
}