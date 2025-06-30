#!/bin/bash
clear

# This script provides a comprehensive overview of changes in the git repository,
# including staged, unstaged, and commit-to-commit differences for both
# files and directories.

# echo "--- Staged Changes (Ready for Commit) ---"
# echo -e "\n[+] Staged Added Files:"
# git diff --cached --name-only --diff-filter=A
# echo -e "\n[*] Staged Modified Files:"
# git diff --cached --name-only --diff-filter=M
# echo -e "\n[-] Staged Deleted Files:"
# git diff --cached --name-only --diff-filter=D
# echo -e "\n"

# echo "--- Unstaged Changes (Not Yet Staged) ---"
# echo -e "\n[*] Unstaged Modified Files:"
# git diff --name-only --diff-filter=M
# echo -e "\n[-] Unstaged Deleted Files:"
# git diff --name-only --diff-filter=D
# echo -e "\n[?] Untracked New Files:"
# git ls-files --others --exclude-standard
# echo -e "\n"

echo "--- Changes in Last Commit (HEAD vs HEAD~1) ---"
echo -e "\n[+] Added Files in Last Commit:"
git diff --name-only --diff-filter=A HEAD~1 HEAD
echo -e "\n[*] Modified Files in Last Commit:"
git diff --name-only --diff-filter=M HEAD~1 HEAD
echo -e "\n[-] Deleted Files in Last Commit:"
git diff --name-only --diff-filter=D HEAD~1 HEAD
echo -e "\n"

echo -e "[+] Added Directories in Last Commit:"
# Compare the directory trees of the last two commits to find added directories
comm -13 <(git ls-tree -r -d --name-only HEAD~1 | sort) <(git ls-tree -r -d --name-only HEAD | sort)
echo -e "\n"

echo -e "[-] Deleted Directories in Last Commit:"
# Compare the directory trees of the last two commits to find deleted directories
comm -23 <(git ls-tree -r -d --name-only HEAD~1 | sort) <(git ls-tree -r -d --name-only HEAD | sort)
echo -e "\n" 