package stack;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @ClassName: StackDemo1
 * @Description: 中缀表达式的计算
 * @Author: xiedong
 * @Date: 2020/9/16 11:51
 */
public class StackDemo1 {
    public static final String regexNumber = "^-?\\d+(\\.\\d+)?$";//数字正则
    private static Map<String, Integer> orderMap = new HashMap();
    private static final Integer errorNum = Integer.MAX_VALUE;

    static {
        orderMap.put("+", 0);
        orderMap.put("-", 0);
        orderMap.put("*", 1);
        orderMap.put("/", 1);
        orderMap.put("(", 2);
        orderMap.put(")", 3);
    }

    public static void main(String[] args) {
        //逆波兰表达式
        String source = "15711+-/3*211++-";
        StringBuilder suffixExpression = new StringBuilder();
        //操作数栈
        Stack<String> operationStack = new Stack<>();
        //运算符栈
        Stack<String> arithmeticStack = new Stack<>();
        //处理源
        getOpLists(source).stream().forEach(per -> {
                    //获取扫描对应优先级
                    Integer val = orderMap.getOrDefault(per, errorNum);
                    if (Pattern.compile(regexNumber).matcher(per).matches()) {
                        //如果是操作数则直接压入操作数栈
                        operationStack.push(per);
                        //直接加入后缀表达式
                        suffixExpression.append(per);
                    } else if (val <= 1) {
                        //如果是运算符：+-*/
                        if (arithmeticStack.isEmpty())
                            //栈空则直接入栈
                            arithmeticStack.push(per);
                        else {
                            //查看当前栈顶元素的优先级
                            int peekNum = orderMap.get(arithmeticStack.peek());
                            //当栈顶运算符优先级高于或等于当前运算符的优先级且栈顶元素不为左括号(
                            while (peekNum >= orderMap.get(per) && peekNum <= 1) {
                                //依次弹出栈顶元素--即运算符
                                String pop = arithmeticStack.pop();
                                //弹出操作数栈栈顶元素---对应右操作数
                                String rightOperNum = operationStack.pop();
                                //弹出操作数栈栈顶元素---对应右操作数
                                String leftOperNum = operationStack.pop();
                                //加入后缀表达式（逆波兰表达式）
                                suffixExpression.append(pop);
                                //左右操作数执行对应运算
                                String tmpResult = doCalculate(pop, leftOperNum, rightOperNum);
                                //运算结果压回操作数栈
                                operationStack.push(tmpResult);
                                //查看当前栈顶元素的优先级
                                peekNum = orderMap.get(arithmeticStack.peek());
                            }
                            //最后当前运算符压入运算符栈
                            arithmeticStack.push(per);
                        }
                    } else if (val == 2) //左括号直接压入运算符栈
                        arithmeticStack.push(per);
                    else if (val == 3) {//右括号
                        String pop = arithmeticStack.pop();
                        Integer popMapNum = orderMap.get(pop);
                        //遇到右括号则依次弹出运算符栈元素，直到弹出左括号"("为止
                        while (popMapNum != 2) {
                            //每次弹出一个 运算符，就必须弹出两个操作数执行对应运算，并将结果压回操作数栈中
                            String rightOperNum = operationStack.pop();
                            String leftOperNum = operationStack.pop();
                            //弹出的运算符加入逆波兰表达式
                            suffixExpression.append(pop);
                            //左右操作数执行对应运算
                            String tmpResult = doCalculate(pop, leftOperNum, rightOperNum);
                            //运算结果压回操作数栈
                            operationStack.push(tmpResult);
                            //再次弹出运算符栈---查看是否为左括号"("
                            pop = arithmeticStack.pop();
                            popMapNum = orderMap.get(pop);
                        }
                    }
                }
        );
        //当扫描完所有字符后，依次弹出运算符栈元素，期间每弹出一个运算符，就需要弹出两个操作数进行相应运算，并将结果压回操作数栈
        while (!arithmeticStack.isEmpty()) {
            //依次弹出运算符栈元素
            String pop = arithmeticStack.pop();
            //加入逆波兰表达式
            suffixExpression.append(pop);
            //期间每弹出一个运算符，就需要弹出两个操作数进行相应运算
            String rightNum = operationStack.pop();
            String leftNum = operationStack.pop();
            String tmpResult = doCalculate(pop, leftNum, rightNum);
            //运算结果压回操作数栈中
            operationStack.push(tmpResult);
        }
        System.out.println("结果是:" + operationStack.peek());
        System.out.println("逆波兰表达式:" + suffixExpression.toString());
    }

    /**
     * 分割源字符串
     *
     * @param source 源字符串
     * @return
     */
    private static List<String> getOpLists(String source) {
        ArrayList<String> res = new ArrayList<>();
        char[] charArray = source.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < charArray.length; i++) {
            sb.append(charArray[i]);
            if (Character.isDigit(charArray[i]) &&
                    i != charArray.length - 1 &&
                    Character.isDigit(charArray[i + 1])) {
                continue;
            } else {
                res.add(sb.toString());
                sb.delete(0, sb.length());
            }
        }
        return res;
    }

    /**
     * 按对应运算符执行运算
     *
     * @param pop          运算符
     * @param leftOperNum  左操作数
     * @param rightOperNum 右操作数
     * @return
     */
    private static String doCalculate(String pop, String leftOperNum, String rightOperNum) {
        BigDecimal res = BigDecimal.ZERO;
        switch (pop) {
            case "+":
                res = new BigDecimal(leftOperNum).add(new BigDecimal(rightOperNum));
                break;
            case "-":
                res = new BigDecimal(leftOperNum).subtract(new BigDecimal(rightOperNum));
                break;
            case "*":
                res = new BigDecimal(leftOperNum).multiply(new BigDecimal(rightOperNum));
                break;
            case "/":
                res = new BigDecimal(leftOperNum).divide(new BigDecimal(rightOperNum));
                break;
        }
        return res.toString();
    }
}
