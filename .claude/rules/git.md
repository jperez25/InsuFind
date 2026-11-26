# Git rules
- Always ask the user for confirmation before every `git commit` and every `git push`. Never do either on your own.
  The confirmation is one validation: show the user the commit message and the files to be committed. Once the user
  approves, the AI may run the `git commit` and the `git push` for that change; any other change needs a new validation.
- Every commit message starts with the project key `ISF-<n>`, where `<n>` is the ticket number, e.g. `ISF-12: add claim search endpoint`.
- If the ticket number is not given (`N=`), read the latest commit (`git log -1`) and use its number plus 1 for the
  current ticket (latest `ISF-12` means this commit is `ISF-13`). Show the resulting message to the user for validation
  before committing. If there is no commit yet or the latest commit has no `ISF-<n>` key, ask the user.
- Never commit `.env` or other secrets.
