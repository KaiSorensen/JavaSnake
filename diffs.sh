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

# echo "--- Changes in Last Commit (HEAD vs HEAD~1) ---"
# echo -e "\n[+] Added Files in Last Commit:"
# git diff --name-only --diff-filter=A HEAD~1 HEAD
# echo -e "\n[*] Modified Files in Last Commit:"
# git diff --name-only --diff-filter=M HEAD~1 HEAD
# echo -e "\n[-] Deleted Files in Last Commit:"
# git diff --name-only --diff-filter=D HEAD~1 HEAD
# echo -e "\n"

echo -e "[+] Added Directories in Last Commit:"
ADDED_FILES=$(git diff --name-only --diff-filter=A HEAD~1 HEAD)
if [ -z "$ADDED_FILES" ]; then
  echo "No new files, so no new directories."
else
  POTENTIAL_DIRS=$(echo "$ADDED_FILES" | xargs -n1 dirname | sort -u | grep -v '^\\.$' || true)
  if [ -z "$POTENTIAL_DIRS" ]; then
    echo "No new directories found."
  else
    NEW_DIRS_FOUND=false
    for dir in $POTENTIAL_DIRS; do
      # Check if the directory existed in the previous commit
      if ! git ls-tree -d HEAD~1 "$dir" >/dev/null 2>&1; then
        echo "$dir"
        NEW_DIRS_FOUND=true
      fi
    done
    if [ "$NEW_DIRS_FOUND" = false ]; then
        echo "No new directories found."
    fi
  fi
fi
echo -e "\n"

echo -e "[-] Deleted Directories in Last Commit:"
# Compare the directory trees of the last two commits to find deleted directories
comm -23 <(git ls-tree -r -d --name-only HEAD~1 | sort) <(git ls-tree -r -d --name-only HEAD | sort)
echo -e "\n" 