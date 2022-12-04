package com.practice;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Calculator extends JFrame {

    private JTextField inputSpace;
    private ArrayList<String> equation = new ArrayList<String>(); // 계산식 임시 저장
    private String num = ""; // 피연산자 임시 저장
    private String prev_operation = ""; // 이전 동작(입력)

    public Calculator() {
        setLayout(null);

        // UI
        inputSpace = new JTextField();
        inputSpace.setEditable(false);
        inputSpace.setBackground(Color.CYAN);
        inputSpace.setHorizontalAlignment(JTextField.RIGHT);
        inputSpace.setFont(new Font("Arial", Font.BOLD, 50));
        inputSpace.setBounds(8, 10, 270, 70);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 4, 10 ,10));
        buttonPanel.setBounds(8, 90, 270, 235);

        String button_names[] = {"C", "÷", "×", "=", "7", "8", "9", "+", "4", "5", "6", "-", "1", "2", "3", "0"};
        JButton buttons[] = new JButton[button_names.length];


        // button matching
        for (int i = 0; i < button_names.length; i++) {
            buttons[i] = new JButton(button_names[i]);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 20));
            if (button_names[i] == "C") {
                buttons[i].setBackground(Color.RED);
            }
            else if ((i >= 4 && i <= 6) || (i >= 8 && i <= 10) || (i >= 12 && i <= 14)) {
                buttons[i].setBackground(Color.BLACK);
            }
            else {
                buttons[i].setBackground(Color.GRAY);
            }
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setBorderPainted(false);
            buttons[i].addActionListener(new PadActionListener());
            buttonPanel.add(buttons[i]);
        }

        add(inputSpace);
        add(buttonPanel);

        setTitle("계산기");
        setVisible(true);
        setSize(300, 370);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // button action listener
    class PadActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) { // button click >> input and display
            // e = button action
            String operation = e.getActionCommand();
            if (operation.equals("C")) { // clear
                inputSpace.setText("");
            } else if (operation.equals("=")) { // result
                String result = Double.toString(calculate(inputSpace.getText()));
                inputSpace.setText("" + result);
                num = "";
            } else if (operation.equals("+") || operation.equals("-") || operation.equals("×") || operation.equals("÷")) { // operator
              if (inputSpace.getText().equals("") && operation.equals("-")) { // 음수입력 예외 처리
                  inputSpace.setText(inputSpace.getText() + e.getActionCommand());
              } else if (!inputSpace.getText().equals("") && !prev_operation.equals("+") && !prev_operation.equals("-") && !prev_operation.equals("×") && !prev_operation.equals("÷")) {
                  inputSpace.setText(inputSpace.getText() + e.getActionCommand()); // previous input != operator or nothing 일 때 연산자 입력 가능
              }
            } else {
                inputSpace.setText(inputSpace.getText() + e.getActionCommand()); // number
            }
            prev_operation = e.getActionCommand();
        }
    }

    private void fullTextParsing(String inputText) { // input parsing
        equation.clear();

        for (int i = 0; i < inputText.length(); i++) {
            char ch = inputText.charAt(i); // 문자 한개씩 가져오기

            if (ch == '-' | ch == '+' | ch == '×' | ch == '÷') { // 먼저 입력된 피연산자 equation 에 추가해주고 num 비워주기
                equation.add(num);
                num = "";
                equation.add(ch + ""); // 연산자 equation 에 추가 (+ "" 해서 char 을 string 으로 변환)
            } else {
                num = num + ch; // 피연산자
            }
        }
        equation.add(num); // 마지막 피연산자 equation 에 추가
        equation.remove(""); // 문자열로 바꿔주기 위한 ""공백 없애주기
    }

    public double calculate(String inputText) { // 계산 동작
        fullTextParsing(inputText);

        double prev = 0; // 이전
        double current = 0; // 현재
        String mode = ""; // operator

        for (int i = 0; i < equation.size(); i++) { // 연산자 우선순위
            String s = equation.get(i); // 계산식 Arraylist 에서 해당 index 항목 가져오기

            if (s.equals("+")) {
                mode = "add";
            } else if (s.equals("-")) {
                mode = "sub";
            } else if (s.equals("×")) {
                mode = "mul";
            } else if (s.equals("÷")) {
                mode = "div";
            } else {
                if ((mode.equals("mul") || mode.equals("div")) && !s.equals("+") && !s.equals("-") && !s.equals("×") && !s.equals("÷")) { // 현재 인덱스에서 연산자가 아니고 mode = mul or div 일 때
                    Double one = Double.parseDouble(equation.get(i - 2)); // 두번째 피연산자 인덱스-2 는 첫번째 피연산자
                    Double two = Double.parseDouble(equation.get(i)); // 두번째 피연산자 ( 현재 인덱스 )
                    Double result = 0.0;

                    if (mode.equals("mul")) {
                        result = one * two;
                    } else if (mode.equals("div")) {
                        result = one / two;
                    }

                    equation.add(i + 1, Double.toString(result)); // 우선 연산한 결과값 저장

                    for (int j = 0; j < 3; j++) {
                        equation.remove(i - 2); // 우선 연산한 식 Arraylist에서 지워주기 ( 현재 인덱스 - 2 부터 현재 인덱스 까지 )
                    }

                    i -= 2; // i - 2 번쨰에서 다시 시작
                }
            }
        }

        for (String s : equation) {
            if (s.equals("+")) {
                mode = "add";
            } else if (s.equals("-")) {
                mode = "sub";
            } else {
                current = Double.parseDouble(s);
                if (mode == "add") {
                    prev += current;
                } else if (mode == "sub") {
                    prev -= current;
                } else {
                    prev = current;
                }
            }
        }
        return prev;
    }

    public static void main(String[] args) {
        new Calculator();
    }
}
