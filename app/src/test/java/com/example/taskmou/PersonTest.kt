package com.example.taskmou

import org.junit.Test
import org.junit.Assert.*
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PersonTest{

    @Test
    fun testEnterTrue() {
        val listPerson = listOf(
            Person("Irina", "ira@mail.ru", "123456"),
            Person("Vlad", "vlad@mail.ru", "111111"),
            Person("Vasilisa", "vasa@mail.ru", "123456")
        )
        val email2 = "ira@mail.ru"
        val pas2 = "123456"

        listPerson.forEach {person ->
            if (person.email == email2 && person.pas == pas2) {
                println("Пользователь ${person.name} вошел успешно")
            } else {
                println("Пользователь ${person.name} не вошел")
            }
        }
    }

    @Test
    fun testEnterFalse() {
        val listPerson = listOf(
            Person("Irina", "ira@mail.ru", "123456"),
            Person("Vlad", "vlad@mail.ru", "111111"),
            Person("Vasilisa", "vasa@mail.ru", "123456")
        )
        val email2 = "mihail@mail.ru"
        val pas2 = "123666"

        listPerson.forEach {person ->
            if (person.email == email2 && person.pas == pas2) {
                println("Пользователь ${person.name} вошел успешно")
            } else {
                println("Пользователь ${person.name} не вошел")
            }
        }
    }
}