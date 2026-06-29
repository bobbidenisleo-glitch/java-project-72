# java-project-72

[![CI](https://github.com/bobbidenisleo-glitch/java-project-72/actions/workflows/ci.yml/badge.svg)](https://github.com/bobbidenisleo-glitch/java-project-72/actions/workflows/ci.yml)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=bobbidenisleo-glitch_java-project-72&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=bobbidenisleo-glitch_java-project-72)

[![Coverage](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/bobbidenisleo-glitch/java-project-72/main/.github/badges/jacoco.json)](https://github.com/bobbidenisleo-glitch/java-project-72/actions)

Веб-приложение для SEO-аудита сайтов. Проверяет страницы на SEO-пригодность: анализирует код ответа, заголовки (title, h1) и мета-описание.

[Deployed App](https://java-project-72-mipy.onrender.com)

## Функциональность

- Добавление URL для анализа
- Проверка HTTP-статуса и мета-данных
- Сохранение истории проверок
- Просмотр всех добавленных сайтов и их последних проверок

## Запуск

```bash
git clone git@github.com:bobbidenisleo-glitch/java-project-72.git
cd java-project-72/app
./gradlew run
