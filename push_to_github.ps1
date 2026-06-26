# ==========================================================================
# AI Software Architect — Git Push Script
# ==========================================================================
# This PowerShell script initializes Git locally, commits all project files,
# configures the remote repository, and pushes the project to GitHub.
#
# PREREQUISITE:
# Please download and install Git from: https://git-scm.com/
#
# RUNNING THE SCRIPT:
# 1. Open PowerShell.
# 2. Run: .\push_to_github.ps1
# ==========================================================================

$ErrorActionPreference = "Stop"

# 1. Verify Git Installation
if (!(Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Host "------------------------------------------------------------------------" -ForegroundColor Red
    Write-Host "ERROR: Git is not installed or not in your system environment PATH." -ForegroundColor Red
    Write-Host "Please download and install Git from https://git-scm.com/ and try again." -ForegroundColor Yellow
    Write-Host "------------------------------------------------------------------------" -ForegroundColor Red
    exit 1
}

# 2. Initialize Git Repository
Write-Host "`n[1/5] Initializing Git repository..." -ForegroundColor Green
if (!(Test-Path .git)) {
    git init
} else {
    Write-Host "Git repository already initialized." -ForegroundColor DarkGray
}

# 3. Create Workspace Root .gitignore (to avoid committing build targets and system logs)
Write-Host "`n[2/5] Setting up workspace root .gitignore..." -ForegroundColor Green
$gitIgnoreContent = @"
# OS-specific files
.DS_Store
Thumbs.db
desktop.ini

# Spring Boot build files
ai-software-architect-backend/target/
ai-software-architect-backend/.mvn/
ai-software-architect-backend/mvnw
ai-software-architect-backend/mvnw.cmd
ai-software-architect-backend/*.log

# React frontend build files
ai-software-architect-frontend/node_modules/
ai-software-architect-frontend/dist/
ai-software-architect-frontend/*.log

# IDE settings
.idea/
.vscode/
*.iml
*.sublime-project
*.sublime-workspace

# System tasks log directories
.system_generated/
"@

Set-Content -Path .gitignore -Value $gitIgnoreContent -Force
Write-Host ".gitignore created successfully." -ForegroundColor DarkGray

# 4. Stage and Commit Files
Write-Host "`n[3/5] Staging and committing project files..." -ForegroundColor Green
git add .
git commit -m "Initial commit - Complete AI Software Architect full-stack project"

# 5. Set Main Branch
Write-Host "`n[4/5] Setting default branch to 'main'..." -ForegroundColor Green
git branch -M main

# 6. Configure Remote GitHub Repository
Write-Host "`n[5/5] Configuring GitHub remote repository..." -ForegroundColor Green
# Suppress error if remote 'origin' doesn't exist yet
try {
    git remote remove origin 2>$null
} catch {}
git remote add origin https://github.com/anmolchandel13/AI-Software-Architect.git

# 7. Push to GitHub
Write-Host "`n=============================================================" -ForegroundColor Cyan
Write-Host "Pushing project to GitHub. A login popup will appear asking you to" -ForegroundColor Yellow
Write-Host "authorize Git Credential Manager to sign in with GitHub." -ForegroundColor Yellow
Write-Host "=============================================================" -ForegroundColor Cyan
git push -u origin main -f

Write-Host "`nProject successfully pushed to: https://github.com/anmolchandel13/AI-Software-Architect 🎉" -ForegroundColor Green
