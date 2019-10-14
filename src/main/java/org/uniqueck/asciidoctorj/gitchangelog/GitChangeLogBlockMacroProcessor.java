package org.uniqueck.asciidoctorj.gitchangelog;

import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.uniqueck.asciidoctorj.exceptions.AsciiDoctorJGitChangeLogRuntimeException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Name("changelog")
public class GitChangeLogBlockMacroProcessor extends BlockMacroProcessor {



    // TODO use attribute to define location of git repository
    // TODO use target to define path for git log command
    // TODO defined attributes for column captions
    protected List<String> generateAsciiDocMarkup(StructuralNode parent, File sourceFile, Map<String, Object> attributes) {
        List<String> contentList = new ArrayList<>();

        Git git = null;
        try {
            git = Git.open(sourceFile);
            LogCommand logCommand = git.log();
            logCommand.setMaxCount(getMacCount(attributes));
            Iterable<RevCommit> revCommits = logCommand.call();
            contentList.add("|===");
            for (RevCommit commit : revCommits) {
                contentList.add("|" + getDateTimeFormatter(attributes).format(LocalDateTime.ofInstant(commit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault())));
                contentList.add("|" + commit.getAuthorIdent().getName());
                contentList.add("|" + commit.getShortMessage());
            }
            contentList.add("|===");
        } catch (GitAPIException | IOException e) {
            throw new AsciiDoctorJGitChangeLogRuntimeException("Error on reading git log", e);
        } finally {
            if (git != null) {
                git.close();
            }
        }
        return contentList;
    }

    protected int getMacCount(Map<String, Object> attributes) {
        return Integer.parseInt((String) attributes.getOrDefault("max-log-count", "-1"));
    }

    protected DateTimeFormatter getDateTimeFormatter(Map<String, Object> attributes) {
        return DateTimeFormatter.ofPattern((String) attributes.getOrDefault("date-format", "dd.MM.yyyy"));
    }


    @Override
    public Object process(StructuralNode parent, String target, Map<String, Object> attributes) {
        parseContent(parent, generateAsciiDocMarkup(parent, getTargetAsFile(parent, target), attributes));
        return null;
    }

    protected File getBuildDir(StructuralNode structuralNode) {
        Map<Object, Object> globalOptions = structuralNode.getDocument().getOptions();

        String toDir = (String) globalOptions.get("to_dir");
        String destDir = (String) globalOptions.get("destination_dir");
        String buildDir = toDir != null ? toDir : destDir;
        return new File(buildDir);
    }

    protected String getAttribute(StructuralNode structuralNode, String attributeName, String defaultValue) {
        String value = (String) structuralNode.getAttribute(attributeName);

        if (value == null || value.trim().isEmpty()) {
            value = defaultValue;
        }

        return value;
    }


    protected File getTargetAsFile(StructuralNode structuralNode, String target) {
        String docdir = getAttribute(structuralNode, "docdir", "");
        return new File(docdir, target);
    }
}
