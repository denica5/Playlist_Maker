Приложение для поиска треков и составления плейлистов с itunes api

##  Стек технологий
- **Архитектура:** MVVM 
- **Тип проекта:** Многомодульный
- **UI:** Jetpack Compose/View
- **DI:** Koin
- **Network:**Retrofit 
- **База данных:** Room



## Архитектура проекта

Проект реализован с использованием Clean Architecture и MVI.

Слои:
- presentation — Compose UI/View + ViewModel
- domain — Interactor и модели
- data — Repository, Retrofit, Room


<p>
    <img src= "https://imgur.com/D8KUcM6.png" width="210">
    <img src= "https://imgur.com/TJZjqyW.png" width="210">
    <img src= "https://imgur.com/Wt9Rl8W.png" width="210">
    <img src= "https://imgur.com/3wBLo63.png" width="210">
    <img src= "https://imgur.com/2qYrDbo.png" width="210">
    <img src= "https://imgur.com/C2wK6eJ.png" width="210">
    <img src= "https://imgur.com/yD4i3Yq.png" width="210">
    <img src= "https://imgur.com/yD4i3Yq.png" width="210">
</p>
