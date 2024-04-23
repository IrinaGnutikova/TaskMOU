package com.example.taskmou

import org.junit.Test
import org.junit.Assert.*
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PersonTest{

    @Test
    fun testForTrueEnter() {
        val listPerson = listOf(
            Person("Кирилл", "kirill1970@yandex.ru", "111111"),
            Person("Сергей", "serrr@mail.ru", "654321"),
            Person("Анастасия", "nastya@mail.ru", "135791")
        )
        val email2 = "serrr@mail.ru"
        val pas2 = "654321"

        listPerson.forEach {person ->
            if (person.email == email2 && person.pasword == pas2) {
                println("${person.name} вошел(а) успешно")
            } else {
                println("${person.name} не вошел(а)")
            }
        }
    }

    @Test
    fun testForFalseEnter() {
        val listPerson = listOf(
            Person("Кирилл", "kirill1970@yandex.ru", "111111"),
            Person("Сергей", "serrr@mail.ru", "654321"),
            Person("Анастасия", "nastya@mail.ru", "135791")
        )
        val email2 = "polina@mail.ru"
        val pas2 = "88888888"

        listPerson.forEach {person ->
            if (person.email == email2 && person.pasword == pas2) {
                println("${person.name} вошел(а) успешно")
            } else {
                println("${person.name} не вошел(а)")
            }
        }
    }
}