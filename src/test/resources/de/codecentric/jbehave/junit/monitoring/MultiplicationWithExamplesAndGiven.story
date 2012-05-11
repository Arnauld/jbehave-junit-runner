Scenario: Multiplication with tabular parameters

Given the variables:
| name | value |
|    x |     1 |
|    y |     2 |
|    z |     3 |
When all variables are multiplied
Then the result should be 6

Scenario: Something with a composite Step

Given a complex situation
When I multiply x by 3
Then x should equal 15

Scenario: Multiplication

Given a variable x with value <number>
When I multiply x by <factor>
Then x should equal <result>

Examples:
| number | factor | result |
|      1 |      1 |      1 |
|      0 |   1000 |      0 |
|    -10 |      0 |      0 |

Scenario: 2 x 3 success with given stories

GivenStories: 	de/codecentric/jbehave/junit/monitoring/Init.story,
				de/codecentric/jbehave/junit/monitoring/Greetings.story

Given a variable x with value 3
When I multiply x by 2
Then x should equal 6

Scenario: 2 x 3 fail with given stories

GivenStories: 	de/codecentric/jbehave/junit/monitoring/Init.story,
				de/codecentric/jbehave/junit/monitoring/Greetings.story

Given a variable x with value 3
When I multiply x by 2
Then x should equal 7

Scenario: 3 x 3 success 

Given a variable x with value 3
When I multiply x by 3
Then x should equal 9

Scenario: 3 x 3 fail

Given a variable x with value 3
When I multiply x by 3
Then x should equal 10
