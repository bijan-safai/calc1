package com.example.bijan.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.lang.Math;
import java.lang.Double;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView result;
    private Button one;
    private Button two;
    private Button three;
    private Button four;
    private Button five;
    private Button six;
    private Button seven;
    private Button eight;
    private Button nine;
    private Button zero;
    private Button add;
    private Button subtract;
    private Button negate;
    private Button divide;
    private Button mulitply;
    private Button exponent;
    private Button enter;
    private Button clear;
    private Button decimal;
    private Button rightP;
    private Button leftP;
    String equation;
    char[] precedence = {'(', '^', 'x', '/', '+', 's'};

    boolean error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // define all buttons and listener for the buttons

        one = (Button) findViewById(R.id.button_one);
        one.setOnClickListener(this);
        two = (Button) findViewById(R.id.button_two);
        two.setOnClickListener(this);
        three = (Button) findViewById(R.id.button_three);
        three.setOnClickListener(this);
        four = (Button) findViewById(R.id.button_four);
        four.setOnClickListener(this);
        five = (Button) findViewById(R.id.button_five);
        five.setOnClickListener(this);
        six = (Button) findViewById(R.id.button_six);
        six.setOnClickListener(this);
        seven = (Button) findViewById(R.id.button_seven);
        seven.setOnClickListener(this);
        eight = (Button) findViewById(R.id.button_eight);
        eight.setOnClickListener(this);
        nine = (Button) findViewById(R.id.button_nine);
        nine.setOnClickListener(this);
        zero = (Button) findViewById(R.id.button_zero);
        zero.setOnClickListener(this);
        add = (Button) findViewById(R.id.button_add);
        add.setOnClickListener(this);
        subtract = (Button) findViewById(R.id.button_subtract);
        subtract.setOnClickListener(this);
        mulitply = (Button) findViewById(R.id.button_multiply);
        mulitply.setOnClickListener(this);
        divide = (Button) findViewById(R.id.button_divide);
        divide.setOnClickListener(this);
        exponent = (Button) findViewById(R.id.button_exponent);
        exponent.setOnClickListener(this);
        enter = (Button) findViewById(R.id.button_enter);
        enter.setOnClickListener(this);
        negate = (Button) findViewById(R.id.button_negate);
        negate.setOnClickListener(this);
        clear = (Button) findViewById(R.id.button_clear);
        clear.setOnClickListener(this);
        decimal = (Button) findViewById(R.id.button_decimal);
        decimal.setOnClickListener(this);
        rightP = (Button) findViewById(R.id.button_rightP);
        rightP.setOnClickListener(this);
        leftP = (Button) findViewById(R.id.button_leftP);
        leftP.setOnClickListener(this);
        result = (TextView) findViewById(R.id.result);

        equation = "";

        error = false;

    }// end of onCreate

    public void onClick(View v) {
        String display = "";
        if (v instanceof Button) {
            if(error == true){
                equation = "";
                display = "";
                error = false;
            }
            if (v.getId() == enter.getId()) {                                                         // user presses enter
                calculate();
            } else if (v.getId() == clear.getId()) {                                     // user presses clear
                equation = "";
                display = "";
            } else if(v.getId() == negate.getId()) {                         //user presses an opearation
                equation += "-";
                display += " -";

            }else if(v.getId() == subtract.getId()){
                equation += 's';
                display += "-";
            }else{
                equation += ((Button) v).getText().toString();
                display += ((Button) v).getText().toString();
            }
        }
        display = equation;
        if(!error) {
            display = display.replace('s', '-');
        }
        result.setText(display);

    }// end of onClick


    public void calculate() {
        try {
            equation = sanitize(equation);
            equation = processString(equation);
            if(Double.parseDouble(equation) == Math.floor(Double.parseDouble(equation))){
                equation = ""+ (int) Math.floor(Double.parseDouble(equation));
            }
        }catch(IllegalArgumentException e){
            equation = e.getMessage();
            error = true;
        }

    }// end of calculate
// looks for typos which are not guaranteed to be caught by other methods
    public String sanitize(String target) throws IllegalArgumentException{
        int open_parentheses = 0;
        for(int i = 0; i < target.length(); i++){
            if(target.charAt(i) == '('){
                open_parentheses++;
                if(!(i-1 < 0)){                              // case of just a number such as 4(3+3)
                    if(!isOperator(target.charAt(i-1))){
                        target = target.substring(0, i) + "x" + target.substring(i, target.length());
                        open_parentheses--;                             // because i is now on the x and will see the ( again
                    }
                }
            }else if(target.charAt(i) == ')'){
                open_parentheses--;
                if(i+1 != target.length()){                              // case of just a number such as 4(3+3)
                    if(!isOperator(target.charAt(i+1))){
                        target = target.substring(0, i+1) + "x" + target.substring(i+1, target.length());
                    }
                }
            }
        }
        if(open_parentheses  > 0){
            throw new IllegalArgumentException("Missing ')'");
        }else if(open_parentheses < 0){
            throw new IllegalArgumentException("Missing '('");
        }
        return target;
    }//end of sanitize



    public String processString(String target) throws IllegalArgumentException {
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < target.length(); j++) {
                    if (precedence[i] == target.charAt(j)) {
                        String temp = "";
                        switch (i) {
                                case 0: temp = parentheses(target, j);                              //found parentheses
                                int end = lookForParentheses(target, j);
                                target = insertSubString(target, temp, j, end);
                                break;
                            case 1: temp = exponent(target, j);                                     //found exponent
                                target = insertSubString(target, temp, findLeftOperand(target, j), findRightOperand(target, j));
                                break;
                            case 2: temp = multiply(target, j);                                     //found x
                                target = insertSubString(target, temp, findLeftOperand(target, j), findRightOperand(target, j));
                                break;
                            case 3: temp = divide(target, j);                                       //found divide
                                target = insertSubString(target, temp, findLeftOperand(target, j), findRightOperand(target, j));
                                break;
                            case 4: temp = add(target, j);                                          //found add
                                target = insertSubString(target, temp, findLeftOperand(target, j), findRightOperand(target, j));
                                break;
                            case 5: temp = subtract(target, j);                                     //found subtract
                                target = insertSubString(target, temp, findLeftOperand(target, j), findRightOperand(target, j));
                                break;
                        }
                    }

                }
            }
        return target;
    }//end of processString

    public String insertSubString(String target, String replacement, int start, int end){
        String to_be_replaced = target.substring(start, end+1);
        return target.replace(to_be_replaced, replacement);
    }


// next two methods handle parentheses case
    public String parentheses(String target, int start) throws IllegalArgumentException{
        int end = lookForParentheses(target, start);
        if (end == -1) {
            throw new IllegalArgumentException("No closing parentheses");
        }
        return processString(target.substring(start+1, end));
    }// end of parentheses

    public int lookForParentheses(String target, int start) {
        int i = start;
        int layers = 0;
        while (i < target.length()) {
            if(target.charAt(i) == '('){
                layers++;
            }
            if (target.charAt(i) == ')') {
                layers--;
                if(layers == 0) {
                    return i;
                }
            }
            i++;
        }
        return -1; // if no closed parentheses is found

    }// end of lookForParentheses
// end of parentheses case
// to raise n to the k
    public String exponent(String target, int start) throws IllegalArgumentException{
        isValidOperator(target, '^', start);
        int start_index = findLeftOperand(target, start);
        int end_index = findRightOperand(target, start);
        Double base = Double.parseDouble(target.substring(start_index, start));
        Double exponent = Double.parseDouble(target.substring(start+1, end_index+1));
        return "" +Math.pow(base, exponent);

    }//end of exponent

// to multiply
    public String multiply(String target, int start) throws IllegalArgumentException {
        isValidOperator(target, 'x', start);
        int start_index = findLeftOperand(target, start);
        int end_index = findRightOperand(target, start);
        Double dec1 = Double.parseDouble(target.substring(start_index, start));
        Double dec2 = Double.parseDouble(target.substring(start+1, end_index+1));
        return ""+(dec1 * dec2);
    }//end of multiply
// to divide
    public String divide(String target, int start) throws IllegalArgumentException {
        isValidOperator(target, '/', start);
        int start_index = findLeftOperand(target, start);
        int end_index = findRightOperand(target, start);
        Double div = Double.parseDouble(target.substring(start_index, start));
        Double diver = Double.parseDouble(target.substring(start+1, end_index+1));
        if(diver == 0.0){
            throw new IllegalArgumentException("Cannot divide by zero");
         }
        return ""+(div / diver);
}
// to add
    public String add(String target, int start) throws IllegalArgumentException{
        isValidOperator(target, '+', start);
        int start_index = findLeftOperand(target, start);
        int end_index = findRightOperand(target, start);
        Double dec1 = Double.parseDouble(target.substring(start_index, start));
        Double dec2 = Double.parseDouble(target.substring(start+1, end_index+1));
        return ""+(dec1+dec2);
    }// end of add

    public String subtract(String target, int start) throws IllegalArgumentException{
        isValidOperator(target, 's', start);
        int start_index = findLeftOperand(target, start);
        int end_index = findRightOperand(target, start);
        Double dec1 = Double.parseDouble(target.substring(start_index, start));
        Double dec2 = Double.parseDouble(target.substring(start+1, end_index+1));
        return ""+(dec1-dec2);
    }// end of add

    public boolean isOperator(char target) {
        for(int i = 0; i < 6; i++){
            if (target == precedence[i] || target == ')') {
                return true;
            }
        }
        return false;
    }//end of isOperator


    //looks for operator + - ect.
    public int lookForOperator(String target, int start, boolean direction){
        if(direction){  // going right
            for(int i = start; i < target.length(); i++) {
                if (isOperator(target.charAt(i))) {
                    return i;
                }
            }
            }else{  // going left
            for(int i = start; i >= 0; i--){
                if(isOperator(target.charAt(i))){
                    return i;
                }
            }
        }
        return -1;
    }//end of lookForOperator

    //looks for left operand
    public int findLeftOperand(String target, int start){
        int start_index = lookForOperator(target, start-1, false);
        if(start_index == -1) {
            start_index = 0;
        }else{
            start_index++;
        }
        return start_index;
    }
    //looks for right operand
    public int findRightOperand(String target, int start){
        int end_index = lookForOperator(target, start+1, true);
        if(end_index == -1) {
            end_index = target.length()-1;
        }else{
           end_index--;
        }
        return end_index;
    }
    //ensures a called operator is present and has numbers on either side
    public void isValidOperator(String target, char operator, int start) throws IllegalArgumentException{
        if(target.charAt(start) != operator){
            if(operator == 's'){
                operator = '-';                                                                     // since internally s is subtract the s must be changed back to a - for user message
            }
            throw new IllegalArgumentException("No '" + operator + "' found");
        }
        if(start-1 < 0 || isOperator(target.charAt(start-1))){
            throw new IllegalArgumentException("Missing number before '" + operator + "'");
        }
        if(start+1 >= target.length() || isOperator(target.charAt(start+1))){
            throw new IllegalArgumentException("Missing number after '" + operator + "'");
        }
    }


}// end of class


