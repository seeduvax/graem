function setTime(o,t) 
    o:setTime(t[0])
end

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
        ["Mobile Object Trajectory"]={
            class="Trajectory",
            bind={
                time=setTime,
                ["world.body.loc"]=function(a,v)
                    a:setLocation(v)
                end,
            },
            changeOfBasis={
                class="BasisLLAtoJMErel"
            }
        },
        ["Mobile Object"]={
            class="DummyAvatar",
            bind={
                time=setTime,
                ["world.body.loc"]="location",
                ["world.body.q"]="attitude",
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
