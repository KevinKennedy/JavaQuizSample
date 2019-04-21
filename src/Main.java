import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


//
// The main app code
//
public class Main implements ActionListener
{
    // All the questions in the game
    // They are presented in the order they are in this array
    private Question[] questions = new Question[]
    {
        new Question("Capital of Colorado", "Colorado Springs", "Denver", 1),
        new Question("Capital of Washington State", "Olympia", "Seattle", 0),
        new Question("2 + 2", "2", "4", 1),
    };

    // The state of the game
    private enum GameState { IntroScreen, AskingQuestion, ShowingAnswer, GameOver, Help }
    private GameState gameState;

    private int currentQuestionIndex; // index into the questions array
    private int correctAnswerCount; // how many correct answers the user has provided

    //
    // Controls in the user interface that
    // we need access to in other parts of
    // the class
    //

    // Controls in the introduction panel
    private JPanel introPanel;
    private JButton startGameButton;
    private JButton quitButton;
    private JButton helpButton;

    // Controls for the help panel
    private JPanel helpPanel;
    private JButton leaveHelpButton;

    // Controls for the main game
    private JPanel questionPanel;
    private JLabel questionLabel;
    private JButton answer0Button;
    private JButton answer1Button;
    private JLabel messageLabel;
    private JButton nextQuestionButton;
    private JButton goToIntroButton;

    //
    // End of the controls section
    //

    // A message we will show to the user
    // Shows the correct answer as well as the final score
    private String message;

    private Main()
    {
        this.CreateUI();
        this.IntroScreen();
    }

    // Takes the user to the intro screen
    // Called when the app launches as well as when the
    // user leaves the help panel and when they want
    // to restart after finishing a game
    private void IntroScreen()
    {
        this.gameState = GameState.IntroScreen;

        this.UpdateUI();
    }

    private void ShowHelpPanel()
    {
        this.gameState = GameState.Help;

        this.UpdateUI();
    }

    // Get us to the first question
    private void StartGame()
    {
        this.currentQuestionIndex = 0;
        this.correctAnswerCount = 0;
        this.gameState = GameState.AskingQuestion;

        this.UpdateUI();
    }

    // Called when the user presses a button for one of the possible answers
    private void OnAnswerPicked(int answerIndex)
    {
        Question q = this.questions[this.currentQuestionIndex];

        if(q.CorrectAnswerIndex == answerIndex)
        {
            // They got the correct answer
            this.message = "Correct Answer!";
            this.correctAnswerCount++;
        }
        else
        {
            // They got the incorrect answer
            this.message = "Incorrect Answer.  Correct answer was " + q.getCorrectAnswerString();
        }

        // They just answered a question so now we're in the ShowingAnswer state
        this.gameState = GameState.ShowingAnswer;

        // If this was the last question then we're actually in
        // the game over state
        if(this.currentQuestionIndex == this.questions.length - 1)
        {
            // Yup, this was the last question

            this.gameState = GameState.GameOver;
            // The label control supports a little HTML so use it to show a longer message
            this.message =
                    "<html>" + this.message +
                    "<br>Game Over" +
                    "<br>Your score: " + this.correctAnswerCount + "/" + this.questions.length;
        }

        UpdateUI();
    }

    // Called when the user presses the 'next question' button
    private void OnNextQuestion()
    {
        this.currentQuestionIndex++;
        this.gameState = GameState.AskingQuestion;

        UpdateUI();
    }

    // Called after a user presses any button and the game state has been updated
    private void UpdateUI()
    {
        // show/hide the different main panels
        this.introPanel.setVisible(this.gameState == GameState.IntroScreen);
        this.questionPanel.setVisible(this.gameState == GameState.AskingQuestion || this.gameState == GameState.ShowingAnswer || this.gameState == GameState.GameOver);
        this.helpPanel.setVisible(this.gameState == GameState.Help);

        // Do state-specific updates of the UI
        if(this.gameState == GameState.AskingQuestion)
        {
            // Show the current question to the user
            // and setup the answer buttons
            Question q = this.questions[this.currentQuestionIndex];
            this.questionLabel.setText(q.Question);
            this.answer0Button.setEnabled(true);
            this.answer0Button.setText(q.Answer0);
            this.answer1Button.setEnabled(true);
            this.answer1Button.setText(q.Answer1);
            this.nextQuestionButton.setVisible(false);
            this.goToIntroButton.setVisible(false);

            // no message to show so hide the label control
            this.messageLabel.setVisible(false);
        }
        else if(this.gameState == GameState.ShowingAnswer)
        {
            // Show the answer to the user and only give
            // them the option to go to the next question

            this.answer0Button.setEnabled(false);
            this.answer1Button.setEnabled(false);
            this.nextQuestionButton.setVisible(true);
            this.goToIntroButton.setVisible(false);
            this.messageLabel.setText(this.message);
            this.messageLabel.setVisible(true);
        }
        else if(this.gameState == GameState.GameOver)
        {
            // Show the Game Over message to the user and
            // disable all the buttons.  User can only close the
            // window at this point.  A "New Game" button could be
            // added to start a new game.

            this.answer0Button.setEnabled(false);
            this.answer1Button.setEnabled(false);
            this.nextQuestionButton.setVisible(false);
            this.goToIntroButton.setVisible(true);
            this.messageLabel.setText(this.message);
            this.messageLabel.setVisible(true);
        }
    }

    // Creates the main window with all the controls for the UI
    // Controls will be hidden and shown in the UpdateUI function above
    // Some samples of creating UI can be found at:
    // http://www.java2s.com/Code/Java/Swing-JFC/HelloWorldSwing.htm (very simple)
    // http://www.java2s.com/Code/Java/Swing-JFC/ThisexampledemonstratestheuseofJButtonJTextFieldandJLabel.htm
    private void CreateUI()
    {
        JFrame frame = new JFrame("My Quiz");

        // Create a panel that holds all the sub-panels of the UI
        // Only one of the sup-panels should be visible at a time.
        // UpdateUI ensures this
        JPanel mainPanel = new JPanel();
        frame.getContentPane().add(mainPanel);

        this.introPanel = this.CreateIntroPanel();
        mainPanel.add(this.introPanel);

        this.helpPanel = this.CreateHelpPanel();
        mainPanel.add(this.helpPanel);

        this.questionPanel = this.CreateQuestionPanel();
        mainPanel.add(this.questionPanel);

        // Quit the app when escape is pressed.  Cobbled together from
        // stuff found on the Internet
        // https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html
        // https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html
        // https://www.thehelper.net/threads/detecting-the-escape-key-press-in-java.136974/
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "quit");
        mainPanel.getActionMap().put("quit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // When the user closes the window, we should exit the app
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setSize(800,600); // set the default size of the window
        frame.setVisible(true); // show everything to the user
    }

    private JPanel CreateIntroPanel()
    {
        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(0, 1, 0, 20));

        this.startGameButton = new JButton("Start Game");
        this.startGameButton.addActionListener(this);
        grid.add(this.startGameButton);

        this.quitButton = new JButton("Quit");
        this.quitButton.addActionListener(this);
        grid.add(this.quitButton);

        this.helpButton = new JButton("Help");
        this.helpButton.addActionListener(this);
        grid.add(this.helpButton);

        return grid;
    }

    private JPanel CreateHelpPanel()
    {
        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(0, 1, 0, 20));

        JLabel helpLabel = new JLabel("<html>This is helpful text<br>Line2<br>Line3");
        grid.add(helpLabel);

        this.leaveHelpButton = new JButton("Back");
        this.leaveHelpButton.addActionListener(this);
        grid.add(this.leaveHelpButton);

        return grid;
    }

    // Create all the user interface components for asking questions
    private JPanel CreateQuestionPanel()
    {
        // Grid layout for all the controls.
        // See https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html
        // for different layout options
        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(0, 1, 0, 20));

        // Control to show the current question
        this.questionLabel = new JLabel();
        grid.add(questionLabel, 0);

        // Button to show one possible answer
        this.answer0Button = new JButton();
        this.answer0Button.addActionListener(this);
        grid.add(this.answer0Button);

        // Button to show another possible answer
        this.answer1Button = new JButton();
        this.answer1Button.addActionListener(this);
        grid.add(this.answer1Button);

        // Label that shows if the user answered the question correctly
        // Also will show their final score at the end of the game
        this.messageLabel = new JLabel();
        grid.add(this.messageLabel);

        // Button to move to the next question.  This is only shown
        // after the user answers a question and the game is not over
        this.nextQuestionButton = new JButton("Next Question");
        this.nextQuestionButton.addActionListener(this);
        grid.add(this.nextQuestionButton);

        // Button that sends the user back to the intro
        // screen.  Only shown in "GameOver" mode
        this.goToIntroButton = new JButton("Home");
        this.goToIntroButton.addActionListener(this);
        grid.add(this.goToIntroButton);

        return grid;
    }

    // Called when the user presses any button in the UI
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(this.gameState == GameState.IntroScreen)
        {
            if(e.getSource() == this.startGameButton)
            {
                this.StartGame();
            }
            else if(e.getSource() == this.quitButton)
            {
                System.exit(0);
            }
            else if(e.getSource() == this.helpButton)
            {
                this.ShowHelpPanel();
            }
        }
        else if(gameState == GameState.Help)
        {
            if(e.getSource() == this.leaveHelpButton)
            {
                this.IntroScreen();
            }
        }
        else if(this.gameState == GameState.AskingQuestion)
        {
            int selectedAnswer = -1;
            if(e.getSource() == this.answer0Button)
            {
                selectedAnswer = 0;
            }
            else if(e.getSource() == this.answer1Button)
            {
                selectedAnswer = 1;
            }

            if(selectedAnswer != -1)
            {
                this.OnAnswerPicked(selectedAnswer);
            }
        }
        else if(this.gameState == GameState.ShowingAnswer)
        {
            if(e.getSource() == this.nextQuestionButton)
            {
                this.OnNextQuestion();
            }
        }
        else if(this.gameState == GameState.GameOver)
        {
            if(e.getSource() == this.goToIntroButton)
            {
                this.IntroScreen();
            }
        }
    }

    //
    // First code executed in the app.  Gets everything going
    //
    public static void main(String[] args)
    {
        new Main();
    }

}

//
// Class that contains the information for a single question in the game
//
class Question
{
    public String Question;
    public String Answer0;
    public String Answer1;
    public int CorrectAnswerIndex;

    // Constructor
    public Question(String question, String answer0, String answer1, int correctAnswerIndex)
    {
        this.Question = question;
        this.Answer0 = answer0;
        this.Answer1 = answer1;
        this.CorrectAnswerIndex = correctAnswerIndex;
    }

    // Helper function to get the string of the correct answer
    public String getCorrectAnswerString()
    {
        if(this.CorrectAnswerIndex == 0)
        {
            return this.Answer0;
        }
        else if(this.CorrectAnswerIndex == 1)
        {
            return this.Answer1;
        }
        return "Error";
    }
}