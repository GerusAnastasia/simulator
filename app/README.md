Notes
-----

Session fragment is state holder fragment.

State flow:
- Menu Fragment gets persistent state from database and creates first Session Fragment with this state
- Each Session fragment contains state, meanwhile state is 
 - Current semester
 - Current player
 - Current stats
 - Current actions
- Each Session fragment produces next session fragment after updating persistent state

```
    Menu Fragment ----------> Create Game Fragment 
          |                              |
          | Load data from database      | Create initial persistent state
          |                              | Load initial persistent state
          V                              | 
    Session Fragment <-------------------/
          |
         ... 
          |  Calculate new state (newState = current state + action)
          |  Update persistent state
          V
    Session Fragment ------> Menu Fragment (Exit) <-----------------------\
          |                                                               |
          | Calculate new state (newStae = current state + action)        |
          | If newState is FinalState. Update persistent state            |
          |                                                               |
          V                                                               |
   Finish Fragment (WIP) -------------------------------------------------/
              
```  

State nodes functions
-----------

##### Menu Fragment
 - Show Create Game Fragment (as new game)
 - Load selected player persistent data from database
 - Show Session Fragment
 
##### Session Fragment
 - Calculate new state
 - Save persistent state to database
 - Show next Session State
 - Show Finish Fragment

##### Finish Fragment
 - Show menu
 
##### Create Game Fragment
 - Create initial state
 - Save initial state to database 
 - Show Session Fragment with initial state
