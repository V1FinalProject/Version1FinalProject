# PESTLE Analysis — Star Awards Recognition Platform

## Political

- Contractor exclusion as of government guidelines. This requires platform changes if these change. The definition of a contractor varies by jurisdiction — a contractor in Ireland is different to a contractor in India.
- Version 1 operates across different countries with different employment laws meaning the Star Award programme has different political exposure in each.
- Government policy on AI in the workplace is actively evolving across the EU, UK, and Ireland meaning regulatory intervention in how AI is used to inform employment-related decisions is a realistic near-term risk.
- Data localisation requirements differ across Version 1's seven operating countries, affecting where nomination and employee data can be stored and processed.

## Economic

- Attrition costs in the technology sector are high, making a recognition programme economically significant beyond its nominal award values.
- Award values are in seven different currencies across seven different countries meaning exchange rate must be considered, directly affecting the value of recognition. These award values are fixed in the guidelines table but are not indexed to anything. Consequently, their real value erodes over time without a policy decision to update them.
- SaaS pricing for dependencies like BambooHR and Reachdesk is externally set and subject to change, directly affecting programme operating costs.
- Recognition schemes are increasingly expected by employees as a baseline rather than a differentiator, raising the cost of not having one.

## Social

- People have an expectation of "gamified" interaction with digital services.
- Implementing digital solutions may automate away work done by humans and thus reduce employment.
- Contractors cannot give or receive awards, which is visible to everyone on a mixed team made up of contractors and permanent employees.
- Remote and hybrid working across Version 1's locations reduces organic visibility of contributions, making structured recognition more important but harder to ensure is equitable across locations. Employees located in Dublin (EU headquarters) are more likely to have higher nomination rates due to proximity and visibility.
- Cultural attitudes toward public recognition vary significantly across Ireland, India, Australia, Spain, Slovenia, the UK, and the USA meaning what feels meaningful in one location may feel uncomfortable in another.

## Technological

- New AI tooling (Claude, etc.) allows rapid prototyping and creation of disposable software systems.
- AI tools introduce bias which can be hard to quantify or detect.
- The AI vendor landscape is changing rapidly meaning models used for tagging today may be deprecated, repriced, or superseded, creating a dependency on external roadmaps outside Version 1's control.
- Reachdesk is a third-party platform whose API, data format, and availability are externally controlled and can change without notice.

## Legal

- Critical infrastructure, biometric identification, and employment screening tools face rigorous data quality, logging, and human oversight rules from new EU directives.
- Liability and security considerations with AI tooling.
- The approval email contains personal data about the nominee written by a third party; GDPR rights of access, correction, and objection for the nominee are not addressed in the current requirements.
- The AI tagging layer influences employment-adjacent decisions; whether the system falls under the EU AI Act's heightened obligations for worker management tools needs to be confirmed.
- The EU AI Act's classification obligations create compliance uncertainty now as implementation guidance is still being published.
- Cross-border payroll notification involves tax and reporting obligations that differ per jurisdiction and are subject to change.

## Environmental

- All computing services consume a certain level of carbon which must be accounted for.
- GPU inference and AI solutions consume large amounts of electricity and carbon for relatively simple tasks.
- Increasing regulatory pressure on organisations to report Scope 3 emissions means software tooling choices, including AI inference, may need to be justified in sustainability reporting. Version 1's clients may increasingly ask about the carbon footprint of custom software solutions delivered on their behalf. As a result, internal tooling choices have external commercial implications.
