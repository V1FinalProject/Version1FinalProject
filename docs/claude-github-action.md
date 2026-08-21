# Claude Access via GitHub Action

This repository uses the [`anthropics/claude-code-action`](https://github.com/anthropics/claude-code-action) to let Claude respond to issues and pull requests directly from GitHub.

## How it works

1. **Trigger** — Two workflows wire Claude into GitHub events (`.github/workflows/claude.yml` and `.github/workflows/claude-code-review.yml`):
   - `claude.yml` fires on `issue_comment`, `pull_request_review_comment`, `pull_request_review`, and `issues` (opened/assigned) events, but only runs when the comment, review, issue body, or issue title contains the trigger phrase `@claude`.
   - `claude-code-review.yml` fires automatically whenever a pull request is opened, updated, or marked ready for review, and runs an automated code review with no trigger phrase required.

2. **Authentication** — The job authenticates using the `CLAUDE_CODE_OAUTH_TOKEN` secret stored in the repository settings. No API key is exposed in the workflow file itself.

3. **Checkout** — The workflow checks out the repository (`actions/checkout@v4`) so Claude has access to the current branch's files.

4. **Execution** — The `anthropics/claude-code-action@v1` step runs Claude Code inside the Actions runner. Claude:
   - Reads the triggering issue/PR/comment content for instructions (or uses a fixed `prompt`, as in the code review workflow).
   - Uses standard Claude Code tools (reading files, editing files, running shell commands, git operations) scoped to the checked-out repository.
   - Posts results back to GitHub by creating or updating a single comment on the issue or PR — this is the only way results are communicated back to users.

5. **Permissions** — The job is granted the minimum GitHub permissions needed (e.g. `contents: read`, `pull-requests: read`, `issues: read`, `id-token: write`, and optionally `actions: read` so Claude can inspect CI results on PRs).

6. **Branch behavior** — When triggered on an issue, Claude creates a new branch for any code changes. When triggered on an open PR, Claude pushes commits directly to that PR's branch. Claude cannot modify files under `.github/workflows/` or perform destructive git operations (force pushes, merges, rebases).

## Customization

Both workflows accept optional `claude_args` and `prompt` inputs to control Claude's behavior (e.g. restricting allowed tools, loading plugins, or supplying a fixed instruction instead of using the triggering comment). See the [claude-code-action usage docs](https://github.com/anthropics/claude-code-action/blob/main/docs/usage.md) for the full list of options.
