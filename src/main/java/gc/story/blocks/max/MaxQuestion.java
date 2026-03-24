package gc.story.blocks.max;

public class MaxQuestion {
    private String question;
    private String answer;

    public MaxQuestion() {
        this.question = "";
        this.answer = "";
    }

    public MaxQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isEmpty() {
        return question.isEmpty() && answer.isEmpty();
    }
}