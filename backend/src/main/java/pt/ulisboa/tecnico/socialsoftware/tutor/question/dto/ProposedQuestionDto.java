package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProposedQuestionDto implements Serializable {
    private Integer id;
    private Integer key;
    private String title;
    private String content;
    private String username;
    private List<OptionDto> options = new ArrayList<>();
    private ImageDto image;
    private String status;
    private String justification;

    public ProposedQuestionDto() {
    }

    public ProposedQuestionDto(ProposedQuestion proposedQuestion) {
        this.id = proposedQuestion.getId();
        this.key = proposedQuestion.getKey();
        this.title = proposedQuestion.getTitle();
        this.content = proposedQuestion.getContent();
        this.username = proposedQuestion.getUsername();
        this.status = proposedQuestion.getStatus().name();
        this.justification = proposedQuestion.getJustification();

        this.options = proposedQuestion.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
        if (proposedQuestion.getImage() != null)
            this.image = new ImageDto(proposedQuestion.getImage());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    @Override
    public String toString() {
        return "ProposedQuestionDto{" +
                "id=" + id +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", image=" + image +
                ", status=" + status + '\'' +
                ", justification=" + justification + '\'' +
                ", options=" + options +
                '}';
    }

}
