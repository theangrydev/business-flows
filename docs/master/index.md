---
title: API Usage Examples
layout: post
---
This is an index of usage examples of the API, with the aim of demonstrating what you can do as a learning aid.
All of these examples were generated from real tests, so you can be confident that the usage shown is up to date.

## BusinessFlow
* [Exception getTechnicalFailure()](BusinessFlow/Exception_getTechnicalFailure--)
* [boolean isTechnicalFailure()](BusinessFlow/boolean_isTechnicalFailure--)

## HappyPath
* [HappyPath&lt;Happy, Sad&gt; happyAttempt(Attempt&lt;Happy&gt;)](HappyPath/HappyPath-Happy,_Sad-_happyAttempt-Attempt-Happy--)
* [HappyPath&lt;Happy, Sad&gt; happyAttempt(Attempt&lt;Happy&gt;, Exception, Sad&gt;)](HappyPath/HappyPath-Happy,_Sad-_happyAttempt-Attempt-Happy-,_Exception,_Sad--)
* [HappyPath&lt;Happy, Sad&gt; happyPath(Happy)](HappyPath/HappyPath-Happy,_Sad-_happyPath-Happy-)
* [HappyPath&lt;Happy, Sad&gt; happyPathAttempt(BusinessFlow&lt;Happy, Sad&gt;&gt;)](HappyPath/HappyPath-Happy,_Sad-_happyPathAttempt-BusinessFlow-Happy,_Sad---)
* [HappyPath&lt;NewHappy, Sad&gt; map(Mapping&lt;Happy, NewHappy&gt;)](HappyPath/HappyPath-NewHappy,_Sad-_map-Mapping-Happy,_NewHappy--)
* [HappyPath&lt;Happy, Sad&gt; sadPath(Sad)](HappyPath/HappyPath-Happy,_Sad-_sadPath-Sad-)
* [HappyPath&lt;Happy, Sad&gt; technicalFailure(Exception)](HappyPath/HappyPath-Happy,_Sad-_technicalFailure-Exception-)