# Reflection on "Coding is NOT solved"

1: Alex argues that AI is a tool and cannot be directly held responsible for bad code that
causes massive failures in the codebase - the blame falls on the user of the AI tools.
He also states that while AI may be fast at generating code, the generation of code itself
is less significant than ensuring it is secure, reliable, scalable, and easily maintainable.
This kind of guarantee about the code requires understanding of the codebase, which cannot
be replaced by AI.

2: Alex claims that the positions where software engineers are needed typically cannot afford
to take on the risk of AI messing up a program and causing catastrophic damages.
They also claim that blindly using AI leads to increased tech debt, new opportunities for
security issues to arise, decreased knowledge of the codebase being worked on, and less incentive
to collaborate with others.

3: I agree with his point about slow being faster when it comes to the overarching software design.
If you take the time to think about what your software actually needs to do and how you are going
to approach this problem, then you will save a lot of time in the future by not having to rewrite/refactor
as much code. You're also going to be less prone to bugs that are deeply ingrained into the system.

4: I disagree with Alex's statement on AI being well equipped for creative/expansive purposes
with regards to visual/audible mediums and summarization of texts. I've seen plenty of instances
of Google's AI attempting to summarize search results and producing inaccurate information while
researching relatively obscure information on Google, and while AI can be suitable for making
decent sketches/videos/sounds quickly, the quality of the output is heavily dependent on what you
prompt for and what details you ask it to include, as well as other factors like
what the AI has been trained on.

# Testing, Documentation

## Changes

I added a Course class to store more information about the course in an object.
Courses have more information than just the course name, so this allows the
program to make distinctions between 2 courses that might have the same name
but are taught by different professors and have different students.

## Impact Analysis

I tested the codebase by running the commands with sample data.
Examples of test situations:
For the adding commands, I made up students and verified in students.txt
that the students were being saved properly.
I used the view commands to verify that they showed the correct information
and that all students were being displayed.
This also doubled as making sure that all data was properly being set
when a Student was initialized.

Because the functions that manipulate student and course data all rely on user input,
it's time consuming to check all possible situations by testing the program with specially
crafted user input. I could have changed this codebase to have separate functions
for just simply doing the operation given the corresponding input versus collecting
user input directly to simplify the testing process in a separate testing file.

If there are any bugs remaining in this program, I suspect there may be one related
to when a student is not currently assigned to a course.