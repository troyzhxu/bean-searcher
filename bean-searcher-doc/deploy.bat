@echo off
setlocal enabledelayedexpansion

:: ============================================
::  Bean Searcher Doc Build & Deploy to OSS
:: ============================================

:: ---- Config ----
set OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com
set OSS_REGION=cn-hangzhou
set OSS_BUCKET=oss://bean-searcher

cd /d "%~dp0"

echo ============================================
echo   Bean Searcher Doc Deploy Script
echo ============================================
echo.

:: ---- Step 1: Verify OSS credentials ----
echo [1/4] Checking OSS credentials ...
:: ossutil reads OSS_ACCESS_KEY_ID / OSS_ACCESS_KEY_SECRET from env vars automatically
if "%OSS_ACCESS_KEY_ID%"=="" (
    echo.
    echo [ERROR] Environment variable OSS_ACCESS_KEY_ID is not set!
    echo         Please set it via: setx OSS_ACCESS_KEY_ID "your-key-id"
    echo         Then restart this terminal.
    pause
    exit /b 1
)
if "%OSS_ACCESS_KEY_SECRET%"=="" (
    echo.
    echo [ERROR] Environment variable OSS_ACCESS_KEY_SECRET is not set!
    echo         Please set it via: setx OSS_ACCESS_KEY_SECRET "your-key-secret"
    echo         Then restart this terminal.
    pause
    exit /b 1
)
echo       OK.
echo.

:: ---- Step 2: yarn build ----
echo [2/4] Building (yarn build) ...
call yarn build
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] yarn build failed! Aborting.
    pause
    exit /b 1
)
echo       Build succeeded.
echo.

:: ---- Step 3: Clear old files from bucket ----
echo [3/4] Clearing old files from bucket ...
ossutil rm %OSS_BUCKET%/ -r -f -e %OSS_ENDPOINT% --region %OSS_REGION%
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Failed to clear bucket!
    pause
    exit /b 1
)
echo       Cleared.
echo.

:: ---- Step 4: Upload dist to OSS ----
echo [4/4] Uploading dist to %OSS_BUCKET% ...
ossutil cp docs/.vitepress/dist/ %OSS_BUCKET%/ -r -f -e %OSS_ENDPOINT% --region %OSS_REGION%
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Upload failed!
    pause
    exit /b 1
)
echo       Uploaded.
echo.

echo ============================================
echo   Deploy SUCCESS!
echo   URL: https://%OSS_BUCKET:~6%.%OSS_ENDPOINT%
echo ============================================
pause
