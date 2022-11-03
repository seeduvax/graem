graem:setup({
    light={class="net.edvuvax.graem.DefaultLight"},
    Playfield={ class="net.eduvax.graem.Playfield" },
    Trajectory={
        class="net.eduvax.graem.Trajectory",
        bind={
            time="time",
            location="world.body.loc",
        },
        cob="net.eduvax.graem.BasisLLAtoJMErel"
    },
    ["Mobile Object"]={
        class="net.eduvax.graem.DummyAvatar",
        bind={
            time="time",
            location="world.body.loc",
            attitude="world.body.q",
            split="split"
        },
        cob="net.eduvax.graem.BasisLLAtoJMErel"
    },
})
