        .globl  mod                     
mod:    enter   $0,$0                   # Start function mod
        movl    8(%ebp),%eax            # x
        pushl   %eax                    
        movl    8(%ebp),%eax            # x
        pushl   %eax                    
        movl    12(%ebp),%eax           # y
        movl    %eax,%ecx               
        popl    %eax                    
        cdq                             
        idivl   %ecx                    # Compute /
        pushl   %eax                    
        movl    12(%ebp),%eax           # y
        movl    %eax,%ecx               
        popl    %eax                    
        imull   %ecx,%eax               # Compute *
        movl    %eax,%ecx               
        popl    %eax                    
        subl    %ecx,%eax               # Compute -
        jmp     .exit$mod               # Return-statement
.exit$mod:
        leave                           
        ret                             # End function mod
        .globl  easter                  
easter: enter   $80,$0                  # Start function easter
        leal    -4(%ebp),%eax           # a
        pushl   %eax                    
        movl    $19,%eax                # 19
        pushl   %eax                    # Push parameter #2
        movl    8(%ebp),%eax            # y
        pushl   %eax                    # Push parameter #1
        call    mod                     # Call mod
        addl    $8,%esp                 # Remove parameters
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -8(%ebp),%eax           # b
        pushl   %eax                    
        movl    8(%ebp),%eax            # y
        pushl   %eax                    
        movl    $100,%eax               # 100
        movl    %eax,%ecx               
        popl    %eax                    
        cdq                             
        idivl   %ecx                    # Compute /
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -12(%ebp),%eax          # c
        pushl   %eax                    
        movl    $100,%eax               # 100
        pushl   %eax                    # Push parameter #2
        movl    8(%ebp),%eax            # y
        pushl   %eax                    # Push parameter #1
        call    mod                     # Call mod
        addl    $8,%esp                 # Remove parameters
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -16(%ebp),%eax          # d
        pushl   %eax                    
        movl    -8(%ebp),%eax           # b
        pushl   %eax                    
        movl    $4,%eax                 # 4
        movl    %eax,%ecx               
        popl    %eax                    
        cdq                             
        idivl   %ecx                    # Compute /
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -20(%ebp),%eax          # e
        pushl   %eax                    
        movl    $4,%eax                 # 4
        pushl   %eax                    # Push parameter #2
        movl    -8(%ebp),%eax           # b
        pushl   %eax                    # Push parameter #1
        call    mod                     # Call mod
        addl    $8,%esp                 # Remove parameters
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -24(%ebp),%eax          # f
        pushl   %eax                    
        movl    -8(%ebp),%eax           # b
        pushl   %eax                    
        movl    $8,%eax                 # 8
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    
        movl    $25,%eax                # 25
        movl    %eax,%ecx               
        popl    %eax                    
        cdq                             
        idivl   %ecx                    # Compute /
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -28(%ebp),%eax          # g
        pushl   %eax                    
        movl    -8(%ebp),%eax           # b
        pushl   %eax                    
        movl    -24(%ebp),%eax          # f
        movl    %eax,%ecx               
        popl    %eax                    
        subl    %ecx,%eax               # Compute -
        pushl   %eax                    
        movl    $1,%eax                 # 1
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    
        movl    $3,%eax                 # 3
        movl    %eax,%ecx               
        popl    %eax                    
        cdq                             
        idivl   %ecx                    # Compute /
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -32(%ebp),%eax          # h
        pushl   %eax                    
        movl    $30,%eax                # 30
        pushl   %eax                    # Push parameter #2
        movl    $19,%eax                # 19
        pushl   %eax                    
        movl    -4(%ebp),%eax           # a
        movl    %eax,%ecx               
        popl    %eax                    
        imull   %ecx,%eax               # Compute *
        pushl   %eax                    
        movl    -8(%ebp),%eax           # b
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    
        movl    -16(%ebp),%eax          # d
        movl    %eax,%ecx               
        popl    %eax                    
        subl    %ecx,%eax               # Compute -
        pushl   %eax                    
        movl    -28(%ebp),%eax          # g
        movl    %eax,%ecx               
        popl    %eax                    
        subl    %ecx,%eax               # Compute -
        pushl   %eax                    
        movl    $15,%eax                # 15
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    # Push parameter #1
        call    mod                     # Call mod
        addl    $8,%esp                 # Remove parameters
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -36(%ebp),%eax          # i
        pushl   %eax                    
        movl    -12(%ebp),%eax          # c
        pushl   %eax                    
        movl    $4,%eax                 # 4
        movl    %eax,%ecx               
        popl    %eax                    
        cdq                             
        idivl   %ecx                    # Compute /
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -40(%ebp),%eax          # k
        pushl   %eax                    
        movl    $4,%eax                 # 4
        pushl   %eax                    # Push parameter #2
        movl    -12(%ebp),%eax          # c
        pushl   %eax                    # Push parameter #1
        call    mod                     # Call mod
        addl    $8,%esp                 # Remove parameters
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -44(%ebp),%eax          # l
        pushl   %eax                    
        movl    $7,%eax                 # 7
        pushl   %eax                    # Push parameter #2
        movl    $32,%eax                # 32
        pushl   %eax                    
        movl    $2,%eax                 # 2
        pushl   %eax                    
        movl    -20(%ebp),%eax          # e
        movl    %eax,%ecx               
        popl    %eax                    
        imull   %ecx,%eax               # Compute *
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    
        movl    $2,%eax                 # 2
        pushl   %eax                    
        movl    -36(%ebp),%eax          # i
        movl    %eax,%ecx               
        popl    %eax                    
        imull   %ecx,%eax               # Compute *
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    
        movl    -32(%ebp),%eax          # h
        movl    %eax,%ecx               
        popl    %eax                    
        subl    %ecx,%eax               # Compute -
        pushl   %eax                    
        movl    -40(%ebp),%eax          # k
        movl    %eax,%ecx               
        popl    %eax                    
        subl    %ecx,%eax               # Compute -
        pushl   %eax                    # Push parameter #1
        call    mod                     # Call mod
        addl    $8,%esp                 # Remove parameters
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -48(%ebp),%eax          # m
        pushl   %eax                    
        movl    -4(%ebp),%eax           # a
        pushl   %eax                    
        movl    $11,%eax                # 11
        pushl   %eax                    
        movl    -32(%ebp),%eax          # h
        movl    %eax,%ecx               
        popl    %eax                    
        imull   %ecx,%eax               # Compute *
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    
        movl    $22,%eax                # 22
        pushl   %eax                    
        movl    -44(%ebp),%eax          # l
        movl    %eax,%ecx               
        popl    %eax                    
        imull   %ecx,%eax               # Compute *
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    
        movl    $451,%eax               # 451
        movl    %eax,%ecx               
        popl    %eax                    
        cdq                             
        idivl   %ecx                    # Compute /
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -52(%ebp),%eax          # month
        pushl   %eax                    
        movl    -32(%ebp),%eax          # h
        pushl   %eax                    
        movl    -44(%ebp),%eax          # l
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    
        movl    $7,%eax                 # 7
        pushl   %eax                    
        movl    -48(%ebp),%eax          # m
        movl    %eax,%ecx               
        popl    %eax                    
        imull   %ecx,%eax               # Compute *
        movl    %eax,%ecx               
        popl    %eax                    
        subl    %ecx,%eax               # Compute -
        pushl   %eax                    
        movl    $114,%eax               # 114
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    
        movl    $31,%eax                # 31
        movl    %eax,%ecx               
        popl    %eax                    
        cdq                             
        idivl   %ecx                    # Compute /
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -52(%ebp),%eax          # month
        pushl   %eax                    
        movl    -32(%ebp),%eax          # h
        pushl   %eax                    
        movl    $1,%eax                 # 1
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    
        movl    $7,%eax                 # 7
        movl    %eax,%ecx               
        popl    %eax                    
        subl    %ecx,%eax               # Compute -
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        leal    -76(%ebp),%eax          # day
        pushl   %eax                    
        movl    $31,%eax                # 31
        pushl   %eax                    # Push parameter #2
        movl    -32(%ebp),%eax          # h
        pushl   %eax                    
        movl    -44(%ebp),%eax          # l
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    
        movl    $7,%eax                 # 7
        pushl   %eax                    
        movl    -48(%ebp),%eax          # m
        movl    %eax,%ecx               
        popl    %eax                    
        imull   %ecx,%eax               # Compute *
        movl    %eax,%ecx               
        popl    %eax                    
        subl    %ecx,%eax               # Compute -
        pushl   %eax                    
        movl    $114,%eax               # 114
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        pushl   %eax                    # Push parameter #1
        call    mod                     # Call mod
        addl    $8,%esp                 # Remove parameters
        pushl   %eax                    
        movl    $1,%eax                 # 1
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        popl    %edx                    
        movl    %eax,(%edx)             #   =
                                        # Start if-statement
        movl    -52(%ebp),%eax          # month
        pushl   %eax                    
        movl    $3,%eax                 # 3
        popl    %ecx                    
        cmpl    %eax,%ecx               
        movl    $0,%eax                 
        sete    %al                     # Test ==
        cmpl    $0,%eax                 
        je      .L0002                  
        movl    $0,%eax                 # 0
        leal    -72(%ebp),%edx          # m_name[...]
        leal    (%edx,%eax,4),%eax      
        pushl   %eax                    
        movl    $77,%eax                # 77
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        movl    $1,%eax                 # 1
        leal    -72(%ebp),%edx          # m_name[...]
        leal    (%edx,%eax,4),%eax      
        pushl   %eax                    
        movl    $97,%eax                # 97
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        movl    $2,%eax                 # 2
        leal    -72(%ebp),%edx          # m_name[...]
        leal    (%edx,%eax,4),%eax      
        pushl   %eax                    
        movl    $114,%eax               # 114
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        movl    $3,%eax                 # 3
        leal    -72(%ebp),%edx          # m_name[...]
        leal    (%edx,%eax,4),%eax      
        pushl   %eax                    
        movl    $99,%eax                # 99
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        movl    $4,%eax                 # 4
        leal    -72(%ebp),%edx          # m_name[...]
        leal    (%edx,%eax,4),%eax      
        pushl   %eax                    
        movl    $104,%eax               # 104
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        jmp     .L0001                  
.L0002:                                 #   else-part
        movl    $0,%eax                 # 0
        leal    -72(%ebp),%edx          # m_name[...]
        leal    (%edx,%eax,4),%eax      
        pushl   %eax                    
        movl    $65,%eax                # 65
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        movl    $1,%eax                 # 1
        leal    -72(%ebp),%edx          # m_name[...]
        leal    (%edx,%eax,4),%eax      
        pushl   %eax                    
        movl    $112,%eax               # 112
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        movl    $2,%eax                 # 2
        leal    -72(%ebp),%edx          # m_name[...]
        leal    (%edx,%eax,4),%eax      
        pushl   %eax                    
        movl    $114,%eax               # 114
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        movl    $3,%eax                 # 3
        leal    -72(%ebp),%edx          # m_name[...]
        leal    (%edx,%eax,4),%eax      
        pushl   %eax                    
        movl    $105,%eax               # 105
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        movl    $4,%eax                 # 4
        leal    -72(%ebp),%edx          # m_name[...]
        leal    (%edx,%eax,4),%eax      
        pushl   %eax                    
        movl    $108,%eax               # 108
        popl    %edx                    
        movl    %eax,(%edx)             #   =
.L0001:                                 # End if-statement
        movl    -76(%ebp),%eax          # day
        pushl   %eax                    # Push parameter #1
        call    putint                  # Call putint
        addl    $4,%esp                 # Remove parameters
        movl    $32,%eax                # 32
        pushl   %eax                    # Push parameter #1
        call    putchar                 # Call putchar
        addl    $4,%esp                 # Remove parameters
                                        # Start for-statement
        leal    -80(%ebp),%eax          # ix
        pushl   %eax                    
        movl    $0,%eax                 # 0
        popl    %edx                    
        movl    %eax,(%edx)             #   =
.L0003:                                 
        movl    -80(%ebp),%eax          # ix
        pushl   %eax                    
        movl    $5,%eax                 # 5
        popl    %ecx                    
        cmpl    %eax,%ecx               
        movl    $0,%eax                 
        setl    %al                     # Test <
        cmpl    $0,%eax                 
        je      .L0004                  
        movl    -80(%ebp),%eax          # ix
        leal    -72(%ebp),%edx          # m_name[...]
        movl    (%edx,%eax,4),%eax      
        pushl   %eax                    # Push parameter #1
        call    putchar                 # Call putchar
        addl    $4,%esp                 # Remove parameters
        leal    -80(%ebp),%eax          # ix
        pushl   %eax                    
        movl    -80(%ebp),%eax          # ix
        pushl   %eax                    
        movl    $1,%eax                 # 1
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        jmp     .L0003                  
.L0004:                                 # End for-statement
.exit$easter:
        leave                           
        ret                             # End function easter
        .globl  main                    
main:   enter   $4,$0                   # Start function main
                                        # Start for-statement
        leal    -4(%ebp),%eax           # y
        pushl   %eax                    
        movl    $2010,%eax              # 2010
        popl    %edx                    
        movl    %eax,(%edx)             #   =
.L0005:                                 
        movl    -4(%ebp),%eax           # y
        pushl   %eax                    
        movl    $2020,%eax              # 2020
        popl    %ecx                    
        cmpl    %eax,%ecx               
        movl    $0,%eax                 
        setle   %al                     # Test <=
        cmpl    $0,%eax                 
        je      .L0006                  
        movl    -4(%ebp),%eax           # y
        pushl   %eax                    # Push parameter #1
        call    easter                  # Call easter
        addl    $4,%esp                 # Remove parameters
        movl    $32,%eax                # 32
        pushl   %eax                    # Push parameter #1
        call    putchar                 # Call putchar
        addl    $4,%esp                 # Remove parameters
        movl    -4(%ebp),%eax           # y
        pushl   %eax                    # Push parameter #1
        call    putint                  # Call putint
        addl    $4,%esp                 # Remove parameters
        movl    $10,%eax                # 10
        pushl   %eax                    # Push parameter #1
        call    putchar                 # Call putchar
        addl    $4,%esp                 # Remove parameters
        leal    -4(%ebp),%eax           # y
        pushl   %eax                    
        movl    -4(%ebp),%eax           # y
        pushl   %eax                    
        movl    $1,%eax                 # 1
        movl    %eax,%ecx               
        popl    %eax                    
        addl    %ecx,%eax               # Compute +
        popl    %edx                    
        movl    %eax,(%edx)             #   =
        jmp     .L0005                  
.L0006:                                 # End for-statement
.exit$main:
        leave                           
        ret                             # End function main
