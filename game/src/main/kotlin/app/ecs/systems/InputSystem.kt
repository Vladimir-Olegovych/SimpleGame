package app.ecs.systems

import app.ecs.models.SendEvents
import app.ecs.processors.HotKeysInputProcessor
import app.ecs.processors.LookInputProcessor
import app.ecs.processors.MovementInputProcessor
import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import tools.graphics.input.CycleInputProcessor

class InputSystem: BaseSystem() {

    @Wire
    private lateinit var inputProcessor: CycleInputProcessor

    @Wire
    private lateinit var sendEvents: SendEvents

    override fun initialize() {
        inputProcessor.addProcessor(MovementInputProcessor(sendEvents))
        inputProcessor.addProcessor(HotKeysInputProcessor(sendEvents))
        inputProcessor.addProcessor(LookInputProcessor(sendEvents))
    }

    override fun processSystem() {

    }
}