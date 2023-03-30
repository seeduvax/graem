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
        Axes={ class="Axes" },
        JMERef={
            class="BasisLLAtoJMErel",
            bind={
                v="world.body.loc",
                q="world.body.q",
            }
        },
        ["Mobile Object Trajectory"]={
            class="Trajectory",
            bind={
                time="time",
                [function(a,v)
                    a:setLocation(v)
                end]="JMERef.v",
            },
        },
        ["Mobile Object"]={
            class="DummyAvatar",
            bind={
                location="JMERef.v",
                attitude="JMERef.q",
                split="split"
            },
        },
        simTime={
            class="HudText",
            x=10,y=600,
            bind={
                [function(a,v)
                    local str="t="..v[1].."s"
                    a:setText(str);
                end]="time"
            },
        },
        cam={
            class="AutoChaseCam",
            offsetY=20,
            chase="Axes",
        },
    },
})
