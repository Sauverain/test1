@echo off

echo Compiling Student Management System with XAMPP MySQL...

set FX=%USERPROFILE%\.m2\repository\org\openjfx

javac -encoding UTF-8 ^
-d bin ^
--module-path "%FX%\javafx-controls\21\javafx-controls-21-win.jar;%FX%\javafx-graphics\21\javafx-graphics-21-win.jar;%FX%\javafx-base\21\javafx-base-21-win.jar" ^
--add-modules javafx.controls ^
-cp "%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar" ^
src\com\dashboard\Main.java ^
src\com\dashboard\App.java ^
src\com\dashboard\models\*.java ^
src\com\dashboard\services\*.java ^
src\com\dashboard\views\*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Launching Dashboard Window...

java ^
--module-path "%FX%\javafx-controls\21\javafx-controls-21-win.jar;%FX%\javafx-graphics\21\javafx-graphics-21-win.jar;%FX%\javafx-base\21\javafx-base-21-win.jar" ^
--add-modules javafx.controls ^
-cp "bin;%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar" ^
com.dashboard.Main

pause