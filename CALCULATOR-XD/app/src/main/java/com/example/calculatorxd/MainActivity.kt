package com.example.calculatorxd

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var textProblem: TextView
    private lateinit var textResult: TextView

    private var currentExpression = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textProblem = findViewById(R.id.text_problem)
        textResult = findViewById(R.id.text_result)

        val buttons = arrayOf(
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4,
            R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9,
            R.id.button_plus, R.id.button_minus, R.id.button_multiply, R.id.button_divide,
            R.id.button_openBracket, R.id.button_closeBracket, R.id.button_dot
        )

        for (buttonId in buttons) {
            val button = findViewById<MaterialButton>(buttonId)
            button.setOnClickListener {
                onButtonClick(button.text.toString())
            }
        }

        val buttonC = findViewById<MaterialButton>(R.id.button_c)
        buttonC.setOnClickListener {
            clear()
        }

        val buttonAC = findViewById<MaterialButton>(R.id.button_ac)
        buttonAC.setOnClickListener {
            clearAll()
        }

        val buttonEquals = findViewById<MaterialButton>(R.id.button_equals)
        buttonEquals.setOnClickListener {
            calculateResult()
        }
    }

    private fun onButtonClick(value: String) {
        currentExpression += value
        textProblem.text = currentExpression
    }

    private fun calculateResult() {
        try {
            val result = eval(currentExpression)
            if (result % 1 == 0.0) {
                textResult.text = result.toInt().toString()
            } else {
                textResult.text = result.toString()
            }
        } catch (e: Exception) {
            textResult.text = "Error"
        }
    }


    private fun clear() {
        if (currentExpression.isNotEmpty()) {
            currentExpression = currentExpression.dropLast(1)
            textProblem.text = currentExpression
        }
    }

    private fun clearAll() {
        currentExpression = ""
        textProblem.text = "0"
        textResult.text = "0"
    }

    private fun eval(expression: String): Double {
        return object : Any() {
            var pos = -1
            var ch = ' '

            fun nextChar() {
                ch = if (++pos < expression.length) expression[pos] else '\u0000'
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expression.length) throw RuntimeException("Unexpected: " + ch)
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when (ch) {
                        '+' -> {
                            nextChar()
                            x += parseTerm()
                        }
                        '-' -> {
                            nextChar()
                            x -= parseTerm()
                        }
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when (ch) {
                        '*' -> {
                            nextChar()
                            x *= parseFactor()
                        }
                        '/' -> {
                            nextChar()
                            val denominator = parseFactor()
                            if (denominator == 0.0) throw RuntimeException("Division by zero")
                            x /= denominator
                        }
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (ch == '(') {
                    nextChar()
                    val x = parseExpression()
                    if (ch != ')') throw RuntimeException("Unbalanced parentheses")
                    nextChar()
                    return x
                }
                if (ch in '0'..'9' || ch == '.') {
                    val sb = StringBuilder()
                    while (ch in '0'..'9' || ch == '.') {
                        sb.append(ch)
                        nextChar()
                    }
                    return sb.toString().toDouble()
                }
                throw RuntimeException("Unexpected: " + ch)
            }
        }.parse()
    }
}
