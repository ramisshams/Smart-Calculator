package calculator

import java.math.BigInteger

val map = mutableMapOf<String, BigInteger>()

fun makeSpaces(expression: List<String>) : MutableList<String> {
    val stringExpression = expression.joinToString("")
    val spacedExpression = stringExpression.replace("(?<=\\w)*(?=[^\\w\\s])|(?<=[^\\w\\s])(?=\\w)".toRegex(), "$0 ")
    val mutExpression = spacedExpression.split(" ").toMutableList()
    return mutExpression
}

fun deleteExtra(a: List<String>) : MutableList<String> {
    val infix = a.toMutableList<String>()
    for (i in 0 until a.size) {
        if (infix[i].matches("(\\+)+".toRegex())) {
            infix[i] = "+"
        } else if (infix[i].matches("(\\-)+".toRegex())) {
            if (infix[i].length % 2 == 1) {
                infix[i] = "-"
            } else {
                infix[i] = "+"
            }
        }
    } // deletes extra pluses and minuses
    return infix
}

fun deleteSpaces(a: List<String>) : MutableList<String> {
    val infix = a.toMutableList()
    for (i in 0 until a.size) {
        if (a[i].isEmpty()) {
            infix.remove(a[i])
        }
    }
    return infix
}

fun postfix(a: List<String>) : MutableList<String> {
    val infix = a.toMutableList()
    val postfix = mutableListOf<String>()
    val stack = mutableListOf<String>()
    for (string in infix) {
        if (string.matches("-?[0-9]+|[a-zA-Z]+".toRegex())) {
            postfix.add(string)
        } else if (string.matches("[-+\\*/()]".toRegex())) {
            if (stack.isEmpty()) {
                stack.add(string)
            } else if (stack.isNotEmpty()) {
                if (stack[0] == "(") {
                    stack.add(0, string)
                } else if (precedence(stack[0], string)) {
                    stack.add(0, string)
                } else {
                    while (!precedence(stack[0], string)) {
                        postfix.add(stack[0])
                        stack.removeAt(0)
                        if (stack.isEmpty() || stack[0] == "(") break
                    }
                    stack.add(0, string)
                }
                if (string == ")") {
                    while (stack[0] != "(") {
                        postfix.add(stack[0])
                        stack.removeAt(0)
                        if (stack.isEmpty()) break
                    }
                    stack.remove("(")
                    postfix.remove(")")
                }
            }
        }
    }
    while (stack.isNotEmpty()) {
        postfix.add(stack[0])
        stack.removeAt(0)
    }
    return postfix
}

fun calculate(a: MutableList<String>) : BigInteger {
    val stack = mutableListOf<BigInteger>()
    for (string in a) {
        if (string.matches("[0-9]+".toRegex())) {
            stack.add(0, string.toBigInteger())
        } else if (string.matches("[a-zA-Z]+".toRegex())) {
            stack.add(0, map[string]!!)
        } else if (string.matches("[-+*/]+".toRegex())) {
            if (string.matches("\\+".toRegex())) {
                stack[0] = stack[0] + stack[1]
                stack.removeAt(1)
            } else if (string.matches("-".toRegex())) {
                stack[0] = stack[1] - stack[0]
                stack.removeAt(1)
            } else if (string.matches("\\*".toRegex())) {
                stack[0] = stack[0] * stack[1]
                stack.removeAt(1)
            } else if (string.matches("/".toRegex())) {
                stack[0] = stack[1] / stack[0]
                stack.removeAt(1)
            }
        }
    }
    return stack[0]
}

fun precedence(op1: String, op2: String) : Boolean {
    val precedence = mapOf("+" to 0, "-" to 0, "*" to 1, "/" to 1, "(" to 2, ")" to 2)
    if (precedence[op2]!! > precedence[op1]!!) return true else return false
}

fun newVariable(a : List<String>) : Pair<String, BigInteger> {
    var newPair : Pair<String, BigInteger> = "" to 0.toBigInteger()
    if (a.joinToString("").split("=", limit = 2)[0].matches("[a-zA-Z]+".toRegex())) {
        if (a.joinToString("").split("=", limit = 2)[1].matches("-?[0-9]+".toRegex())) {
            newPair = a.joinToString("").split("=", limit = 2)[0] to a.joinToString("").split("=")[1].toBigInteger()
        } else if (!a.joinToString("").split("=", limit = 2)[1].matches("-?[0-9]+|[a-zA-Z]+".toRegex())) {
            println("Invalid assignment")
        }
    } else {
        println("Invalid identifier")
    }
    return newPair
}

fun reassignment(a : List<String>) : Pair<String, BigInteger> {
    var newPair : Pair<String, BigInteger> = "" to 0.toBigInteger()
    if (a.joinToString("").split("=")[1].matches("[a-zA-Z]+".toRegex())) {
        if (map.containsKey(a.joinToString("").split("=")[1])) {
            newPair = a.joinToString("").split("=")[0] to map[a.joinToString("").split("=")[1]]!!
        } else {
            println("Unknown variable")
        }
    }
    return newPair
}

fun command(a: List<String>) : String {
    if (a.joinToString().contains("/help")) {
        return "The program calculates the variety of operations with numbers"
    } else {
        return "Unknown command"
    }
}

fun main() {
    var numbers = readln().split(" ")
    while (numbers[0] != "/exit") {
        if (numbers.joinToString("").matches("[-+]?([0-9]+|[a-zA-Z]+)(([-+]+|[*/])[)(]*([0-9]+|[a-zA-Z]+))*[)(]*(([-+]+|[*/])[)(]*([0-9]+|[a-zA-Z]+)[)(]*)*".toRegex()) || numbers.size == 1 && numbers[0].matches("[a-zA-Z]+".toRegex())) {
            numbers = deleteExtra(numbers)
            numbers = makeSpaces(numbers)
            numbers = deleteSpaces(numbers)
            if (numbers.contains("(") && !numbers.contains(")") || !numbers.contains("(") && numbers.contains(")")) {
                println("Invalid expression")
            } else if (numbers[0].matches("-|[0-9]+".toRegex()) || (numbers[0].matches("[a-zA-Z]+".toRegex()) && map.containsKey(numbers[0]))) {
                println(calculate(postfix(numbers)))
            } else if (numbers[0].matches("[0-9]+".toRegex()) || (numbers[0].matches("[a-zA-Z]+".toRegex()) && !map.containsKey(numbers[0]))) {
                println("Unknown variable")
                }
            numbers = readln().split(" ")
        } else if (numbers.joinToString("").contains("=")) {
            var (key, value) = newVariable(numbers)
            map.put(key, value)
            var (key1, value1) = reassignment(numbers)
            map.put(key1, value1)
            numbers = readln().split(" ")
        } else if (numbers[0].isEmpty()) {
            numbers = readln().split(" ")
        } else if (numbers.joinToString().startsWith("/")) {
            println(command(numbers))
            numbers = readln().split(" ")
        } else {
            println("Invalid expression")
            numbers = readln().split(" ")
        }
    }
    when (numbers[0]) {
        "/exit" -> println("Bye!")
    }
}
