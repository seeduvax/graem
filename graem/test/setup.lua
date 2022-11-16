graem:setup({
    components={
        light={
            class="net.eduvax.graem.DefaultLight",
            set={
                shadowmapSize=4096
            }
        },
        Playfield={ class="net.eduvax.graem.Playfield" },
        Trajectory={
            class="net.eduvax.graem.Trajectory",
            bind={
                time="time",
                location="world.body.loc",
            },
            set={
                changeOfBasis={
                    class="net.eduvax.graem.BasisLLAtoJMErel"
                }
            }
        },
        ["Mobile Object"]={
            class="net.eduvax.graem.DummyAvatar",
            bind={
                time="time",
                location="world.body.loc",
                attitude="world.body.q",
                split="split"
            },
            set={
                changeOfBasis={
                    class="net.eduvax.graem.BasisLLAtoJMErel"
                }
            }
        },
--        cam={
--            class="net.eduvax.graem.AutoChaseCam",
--        }
    },
})
