### Things I Do For Loot

#### TODO:
- [ ] visual feedback on long touch
- [ ] localization
- [ ] skills & categories
- [ ] app icon
- [ ] landscape mode
- [ ] navigation animations
- [ ] details view
- [ ] overall level
- [ ] save state in all fragments / activities
- [ ] remove edit task button (save in `onSaveInstanceState`)

#### Entities:
##### Task
- [x] id (database id)
- [x] goal (title)
- [x] details
- [x] status (open, done, failed)
- [ ] recurring (none, every [x] day)
- [ ] start time
- [ ] end time
- [ ] notify at (list of notification timestamps)
- [ ] tags
- [x] priority
- [x] duration
- [x] icon
- [x] calculated amount of xp
- [ ] affected skills

##### Priority
- [x] value (trivial, regular, hard, insane)

##### Tag
- [ ] value

##### Notification
- [ ] time
- [ ] task id

##### List<Task>
- [ ] tasks
- [ ] grouped by (priority, end time, tags, status)

##### Skill
- [ ] name
- [ ] description
- [ ] amount of xp
- [ ] level
- [ ] icon

##### Skill Group
- [ ] name
- [ ] description
- [ ] assigned skills
- [ ] summarized amount of xp
- [ ] summarized level
- [ ] icon