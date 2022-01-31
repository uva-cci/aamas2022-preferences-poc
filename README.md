# CP-Net Preferences in BDI Agents
This test project is the proof of concept source for AAMAS2022 paper _Preference-Based Goal Refinement in BDI Agents_

The example is created with the Agent framework [https://github.com/mostafamohajeri/agentscript](ASC2).

## How to run
You need Java 11.0.3+ and sbt 1.5.5+

The main agent file is located in `/src/main/test/`, the test code to run the agent is in `/src/test/scala/OrderFood.scala`.

To compile: `sbt compile`
To run tests: `sbt test`