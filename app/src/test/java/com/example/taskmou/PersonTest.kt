package com.example.taskmou

import org.junit.Test
import org.junit.Assert.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PersonTest{

    @Test
    fun testEnterTrue() {
        val listPerson = listOf(
            Person("Irina", "ira@mail.ru", "123456"),
            Person("Vlad", "vlad@mail.ru", "111111"),
            Person("Vasilisa", "vasa@mail.ru", "654321")
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
}