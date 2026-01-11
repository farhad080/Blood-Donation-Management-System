@echo off
REM Script to reset the database and fix login issues
REM This will delete the existing database and recreate it with default users

echo ========================================
echo Roktim Database Reset Script
echo ========================================
echo.
echo This will delete the existing database and create a fresh one
echo with the correct password hashing.
echo.
pause

REM Delete the database file if it exists
if exist roktim_blood_donation.db (
    echo Deleting existing database...
    del roktim_blood_donation.db
    echo Database deleted.
) else (
    echo No existing database found. A new one will be created.
)

echo.
echo Next, the application will automatically initialize the database
echo with default users when you start it.
echo.
echo ========================================
echo Default Credentials (after reset):
echo ========================================
echo Admin User:
echo   Username: admin
echo   Password: admin123
echo.
echo Donor User:
echo   Username: farhad
echo   Password: 1234
echo ========================================
echo.
pause

