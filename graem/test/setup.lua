graem:setup({
    import={
        "net.eduvax.graem"
    },
    components={
        Sky={
            class="SkyBox",
        },
        Sun={
            class="DirLight",
            shadowmapSize=4096,
            r=0.5, g=0.5, b=0.5,
        },
        Ambiant={
            class="AmbLight",
            shadowmapSize=4096,
            r=0.5, g=0.5, b=0.5,
        },
        Playfield={ class="Playfield" },
        ["Mobile Object Trajectory"]={
            class="Trajectory",
            bind={
                time="time",
                location="world.body.loc",
            },
            changeOfBasis={
                class="BasisLLAtoJMErel"
            }
        },
        ["Mobile Object"]={
            class="DummyAvatar",
            bind={
                time="time",
                location="world.body.loc",
                attitude="world.body.q",
                split="split"
            },
            changeOfBasis={
                class="BasisLLAtoJMErel"
            }
        },
        cam={
            class="AutoChaseCam",
        }
    },
})
