Simple project to process big files of transactions.

For sake of simplicity tests are included in the production files.
A proper end-to-end test can be found in app.clj.

Also for the sake of simplicity DDD layering is only loosely followed.
app.clj directly refers to infrastructure.clj, since I didn't want to bother with dependency inversion for a simple task like this.

Steps to run (modifying main.clj):
1. specify the input
2. uncomment the last line
3. run main.clj