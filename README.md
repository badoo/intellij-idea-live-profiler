Live Profiler plugin
=======================

![logo](images/liveprof_logo.png "logo")

A PhpStorm plugin for [Live profiler](https://github.com/badoo/liveprof). 

Features
---------

* Line markers show a last date when the method was called
* A link to the method usage page in [Live profiler UI](https://github.com/badoo/liveprof-ui)
* List with last app called the method sorted by time
* By default url settings use [Live profiler UI demo site](http://liveprof.org/)

Settings
---------

The plugin has settings in ```Preferences -> Tools -> Live Profiler```

Flag "Enable Live Profiler" to show line markers for each method which has information in Live Profiler

Flag "Enable debug messages" to show debug messages in Event Log

Flag "Show last apps" to load and display a last apps with timers with a link to the graph page. If it is disabled line markers has a link only for the method usage page.

Urls tab contains urls of data source:
* Method URLS - to load full method list with the last usage date
* Method info URL - to load last apps with timers
* Method usage URL - a link to the method usage page
* Graphs URL - a link to a page with the graphs and timers of the method 
___
Preset to use [Live profiler UI demo site](http://liveprof.org/):
* Method URLS - http://liveprof.org/profiler/all-methods.json?uuid=70366397-97d6-41be-a83c-e9e649c824e1&
* Method info URL - http://liveprof.org/profiler/method-used-apps.json?uuid=70366397-97d6-41be-a83c-e9e649c824e1&method=
* Method usage URL - http://liveprof.org/profiler/method-usage.phtml?uuid=70366397-97d6-41be-a83c-e9e649c824e1&method=
* Graphs URL - http://liveprof.org/profiler/tree-view.phtml?uuid=70366397-97d6-41be-a83c-e9e649c824e1&

**uuid** - unique code you can get after login on [Live profiler UI demo site](http://liveprof.org/)
___

Preset to use self hosted UI:
* Method URLS - {%HOST%}/profiler/all-methods.json
* Method info URL - {%HOST%}/profiler/method-used-apps.json?method=
* Method usage URL - {%HOST%}/profiler/method-usage.phtml?method=
* Graphs URL - {%HOST%}/profiler/tree-view.phtml?

IDE Setup 
---------
You will need IDEA Community Edition or IDEA Ultimate.
PhpStorm can't have `Plugin DevKit` installed

1. Checkout source, i.e. ```$ git clone https://github.com/shagtv/intellij-idea-live-profiler.git```
2. Install `Gradle` plugin in IDEA
3. Open project
4. Idea will suggest to import Gradle configuration, agree and chose `Use gradle 'wrapper' task configuration`
5. View -> Tool windows -> Gradle 

Build From IDE
--------------
View -> Tool windows -> Gradle -> Tasks -> intellij -> buildPlugin 

Build From Command line
-----------------------
```
$ ./gradlew --info buildPlugin
```

The plugin will be placed in **build/distributions/**

Run tests from command like
-----------------------
```
$ ./gradlew test
```

Test result report will be placed in **build/reports/tests/test/index.html**
